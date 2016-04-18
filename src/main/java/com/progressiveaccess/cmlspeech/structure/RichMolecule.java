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

package com.progressiveaccess.cmlspeech.structure;

import com.progressiveaccess.cmlspeech.analysis.Heuristics;
import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.sre.XmlVisitor;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Atom sets that form a molecule of their own. I.e., the topmost structure.
 */

public class RichMolecule extends RichAtomSet implements RichSuperSet {

  private final List<RichStructure<?>> blocks = new ArrayList<>();
  private final List<RichAtom> singletonAtoms = new ArrayList<>();
  private ComponentsPositions path = new ComponentsPositions();


  /**
   * Constructs a rich molecule.
   *
   * @param container
   *          The atom container of the molecule.
   * @param id
   *          The name of the molecule.
   */
  public RichMolecule(final IAtomContainer container, final String id) {
    super(container, id, RichSetType.MOLECULE);
    this.singletonAtoms();
    this.subSuperSystems();
    this.walk();
  }


  /**
   * Computes the singleton atoms in the molecule, i.e. those not belonging to
   * any atom set or block in the molecule.
   */
  private void singletonAtoms() {
    final Set<String> atomSetComponents = new HashSet<String>();
    RichStructureHelper.getAtomSets().forEach(
        as -> atomSetComponents.addAll(as.getComponents()));
    for (final RichAtom atom : RichStructureHelper.getAtoms()) {
      if (!atomSetComponents.contains(atom.getId())) {
        this.singletonAtoms.add(atom);
      }
    }
  }


  /**
   * @return Returns the list of singleton atoms.
   */
  public List<RichAtom> getSingletonAtoms() {
    return this.singletonAtoms;
  }


  /** Sets the sub systems of the molecule, and it as their supersystem. */
  private void subSuperSystems() {
    for (final RichAtomSet system : RichStructureHelper.getAtomSets()) {
      if (system.getType() == RichSetType.SMALLEST) {
        continue;
      }
      system.getSuperSystems().add(this.getId());
      this.getSubSystems().add(system.getId());
    }
    for (final RichAtom atom : this.getSingletonAtoms()) {
      atom.getSuperSystems().add(this.getId());
      this.getSubSystems().add(atom.getId());
    }
  }


  @Override
  protected final void walk() {
    this.setPath();
    for (final String structure : this.getPath()) {
      if (RichStructureHelper.isAtom(structure)) {
        this.getComponentsPositions().addNext(structure);
      } else {
        final RichAtomSet atomSet = RichStructureHelper
            .getRichAtomSet(structure);
        atomSet.walk();
        this.getComponentsPositions().putAll(atomSet.getComponentsPositions());
        if (atomSet.getType() == RichSetType.FUSED) {
          for (final String subRing : ((RichFusedRing) atomSet).getPath()) {
            final RichAtomSet subSet = RichStructureHelper
                .getRichAtomSet(subRing);
            this.getComponentsPositions()
              .putAll(subSet.getComponentsPositions());
          }
        }
      }
    }
  }


  @Override
  public ComponentsPositions getPath() {
    return this.path;
  }


  @Override
  public void setPath() {
    this.blocks.addAll(this.getSubSystems().stream()
        .map(RichStructureHelper::getRichStructure)
        .collect(Collectors.toList()));
    Collections.sort(this.blocks,
                     new Heuristics(Cli.hasOption("molcom")
                                    ? Cli.getOptionValue("molcom") : ""));
    final WalkDepthFirst dfs = new WalkDepthFirst(this.blocks);
    dfs.putPositions(this.path);
  }


  @Override
  public void accept(final XmlVisitor visitor) {
    visitor.visit(this);
  }

}
