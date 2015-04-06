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
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich molecule.
 * 
 * 
 */

//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import io.github.egonw.analysis.WeightComparator;
import io.github.egonw.analysis.Heuristics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import io.github.egonw.analysis.RichStructureHelper;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Atom sets that form a molecule of their own. I.e., the topmost structure.
 */

public class RichMolecule extends RichAtomSet implements RichSuperSet {

    private List<RichStructure<?>> blocks = new ArrayList<>();

    public RichMolecule(IAtomContainer container, String id) {
        super(container, id, RichSetType.MOLECULE);
    }


    protected final void walk() {
        this.setPath();
        for (String structure : this.getPath()) {
            if (RichStructureHelper.isAtom(structure)) {
                this.componentPositions.addNext(structure);
            } else {
                RichAtomSet atomSet = RichStructureHelper.getRichAtomSet(structure);
                atomSet.walk();
                this.componentPositions.putAll(atomSet.componentPositions);
                if (atomSet.getType() == RichSetType.FUSED) {
                    for (String subRing : ((RichFusedRing)atomSet).getPath()) {
                        RichAtomSet subSet = RichStructureHelper.getRichAtomSet(subRing);
                        this.componentPositions.putAll(subSet.componentPositions);
                    }
                }
            }
        }
    }

    //TODO (sorge) These parameters should eventually be computed in this class!
    public final void walk(List<RichAtomSet> majorSystems, List<RichAtom> singletonAtoms) {
        this.blocks.addAll(majorSystems);
        this.blocks.addAll(singletonAtoms);
        this.walk();
    }


    private ComponentsPositions path = new ComponentsPositions();


    @Override
    public ComponentsPositions getPath() {
        return path;
    }
    
    @Override
    public void setPath() {
        Collections.sort(blocks, new Heuristics(""));
        WalkDepthFirst dfs = new WalkDepthFirst(blocks);
        this.path = dfs.getPositions();
    }

}
