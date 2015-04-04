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

    public RichMolecule(IAtomContainer container, String id) {
        super(container, id, RichSetType.MOLECULE);
    }


    // Maybe use later to compute majorsystems and singletonatoms.
    //
    // public class TreeSetSupplier implements Supplier<TreeSet<RichStructure<?>>> {
    //     public TreeSet<RichStructure<?>> get() {
    //         return new TreeSet<RichStructure<?>>(new WeightComparator());
    //     }
    // }
    

    // TODO (sorge) This should eventually become the major path computation.
    protected final void walk() {
    }

    //TODO (sorge) These parameters should eventually be computed in this class!
    public final void walk(List<RichAtomSet> majorSystems, List<RichAtom> singletonAtoms) {
        //TODO (sorge) Replace this with a sorted list
        List<RichStructure<?>> blocks = new ArrayList<>(majorSystems);
        blocks.addAll(singletonAtoms);
        Collections.sort(blocks, new Heuristics(""));
        this.walkDepthFirst(blocks);
    }


    private class OrderComparator<T> implements Comparator<T> {
        private List<T> order;

        public OrderComparator(List<T> order) {
            this.order = order;
        }
        
        public int compare(T object1, T object2) {
            Integer index1 = this.order.indexOf(object1);
            Integer index2 = this.order.indexOf(object2);
            return -1 * Integer.compare(index1, index2);
        }
    }
    

    /** 
     * Depth first traversal of structure.
     * 
     * @param atom The start atom.
     */
    protected final void walkDepthFirst(List<RichStructure<?>> order) {
        OrderComparator<RichStructure<?>> comparator = new OrderComparator<>(order);
        if (order.isEmpty()) {
            return;
        }
        RichStructure<?> first = order.get(0);
        List<RichStructure<?>> visited =  new ArrayList<RichStructure<?>>();
        Stack<RichStructure<?>> frontier = new Stack<RichStructure<?>>();
        frontier.push(first);
        while (!frontier.empty()) {
            RichStructure<?> current = frontier.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            this.path.addNext(current.getId());
            List<RichStructure<?>> elements = current.getConnections().stream()
                .map(con -> RichStructureHelper.getRichStructure(con.getConnected()))
                .filter(order::contains)
                .collect(Collectors.toList());
            Collections.sort(elements, comparator);
            elements.stream().forEach(frontier::push);
        }
    }

    private ComponentsPositions path = new ComponentsPositions();


    @Override
    public ComponentsPositions getPath() {
        return path;
    }
    
    @Override
    public void setPath() {}

}
