// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file   RichMolecule.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Feb 24 17:13:29 2015
 *
 * @brief  Implementation of rich molecule.
 *
 *
 */

//

package io.github.egonw.structure;

import io.github.egonw.analysis.Heuristics;
import io.github.egonw.analysis.RichStructureHelper;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Atom sets that form a molecule of their own. I.e., the topmost structure.
 */

public class RichMolecule extends RichAtomSet implements RichSuperSet {

  private final List<RichStructure<?>> blocks = new ArrayList<>();

  public RichMolecule(final IAtomContainer container, final String id) {
    super(container, id, RichSetType.MOLECULE);
  }

  public final void computePositions() {
    this.walk();
  }

  @Override
  protected final void walk() {
    this.setPath();
    for (final String structure : this.getPath()) {
      if (RichStructureHelper.isAtom(structure)) {
        this.componentPositions.addNext(structure);
      } else {
        final RichAtomSet atomSet = RichStructureHelper
            .getRichAtomSet(structure);
        atomSet.walk();
        this.componentPositions.putAll(atomSet.componentPositions);
        if (atomSet.getType() == RichSetType.FUSED) {
          for (final String subRing : ((RichFusedRing) atomSet).getPath()) {
            final RichAtomSet subSet = RichStructureHelper
                .getRichAtomSet(subRing);
            this.componentPositions.putAll(subSet.componentPositions);
          }
        }
      }
    }
  }

  private ComponentsPositions path = new ComponentsPositions();

  @Override
  public ComponentsPositions getPath() {
    return this.path;
  }

  @Override
  public void setPath() {
    this.blocks.addAll(this.getSubSystems().stream()
        .map(RichStructureHelper::getRichStructure)
        .collect(Collectors.toList()));
    Collections.sort(this.blocks, new Heuristics(""));
    final WalkDepthFirst dfs = new WalkDepthFirst(this.blocks);
    this.path = dfs.getPositions();
  }

}
