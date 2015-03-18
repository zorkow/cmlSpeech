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
 * @file   RichIsolatedRing.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich isolated ring.
 * 
 * 
 */

//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.PriorityQueue;
import io.github.egonw.analysis.WeightComparator;
import java.util.Comparator;
import org.openscience.cdk.tools.AtomicProperties;
import java.io.IOException;
import java.util.Queue;

/**
 * Atom sets that are rich isolated rings.
 */

public class RichIsolatedRing extends RichAtomSet {

    public RichIsolatedRing(IAtomContainer container, String id) {
        super(container, id, RichSetType.ISOLATED);
    }


    protected final void walk() {
        // TODO (sorge) Choose start wrt. replacements, preferring O, then by
        // weight. Then wrt. position OH substitution then other substitutions
        // by weight. For direction: choose second by smallest distance to
        // first!
        Queue<IAtom> internalSubst = this.getInternalSubsts();
        if (internalSubst.size() > 1) {
            this.walkInternalSubst(internalSubst.peek());
            return;
        }
        if (this.getConnectingAtoms().size() == 0) {
            if (internalSubst.size() == 1) {
                this.walkStraight(internalSubst.peek());
                System.out.println(this.orderedAtomNames());
                return;
            }
            this.walkStraight(this.getStructure().getFirstAtom());
            System.out.println(this.orderedAtomNames());
            return;
        }
        //        startAtom ? this.enumerateInternalSystem.out.println(startAtom);
        //this.walkStraight(this.getStructure().atoms().iterator().next());
    }


    private void walkInternalSubst(IAtom startAtom) {
        List<IAtom> queueLeft = new ArrayList<>();
        List<IAtom> queueRight = new ArrayList<>();
        queueLeft.add(startAtom);
        queueRight.add(startAtom);
        List<IAtom> connected = this.getStructure().getConnectedAtomsList(startAtom);
        // This should be of length 2. Otherwise there is a problem.
        IAtom nextLeft = connected.get(0);
        IAtom nextRight = connected.get(1); 
        while (nextLeft != null && nextRight != null) {
            if (!RichIsolatedRing.isCarbon(nextLeft) && !RichIsolatedRing.isCarbon(nextRight)) {
                if ((new InternalSubstComparator()).compare(nextLeft, nextRight) <= 0) {
                    this.walkFinalise(nextLeft, queueLeft);
                    return;
                }
                this.walkFinalise(nextRight, queueRight);
                return;
            }
            if (!RichIsolatedRing.isCarbon(nextLeft)) {
                this.walkFinalise(nextLeft, queueLeft);
                return;
            }
            if (!RichIsolatedRing.isCarbon(nextRight)) {
                this.walkFinalise(nextRight, queueRight);
                return;
            }
            nextLeft = chooseNext(queueLeft, nextLeft);
            nextRight = chooseNext(queueRight, nextRight);
        }
    }

    
    private void walkFinalise(IAtom endAtom, List<IAtom> path) {
        for (IAtom atom : path) {
            this.componentPositions.addNext(atom.getID());
        }
        this.walkStraight(endAtom, path);
    }
        

    /** 
     * Finds the next atom in the ring that has not yet been visited.
     * 
     * @param atom 
     * 
     * @return 
     */
    private IAtom chooseNext(List<IAtom> visited, IAtom atom) {
        visited.add(atom);
        List<IAtom> connected = this.getStructure().getConnectedAtomsList(atom);
        if (!visited.contains(connected.get(0))) {
            return connected.get(0);
        }
        if (visited.size() > 1 && !visited.contains(connected.get(1))) {
            return connected.get(1);
        }
        return null;
    }


    
    private class InternalSubstComparator implements Comparator<IAtom> {
        public int compare (IAtom atom1, IAtom atom2) {
            String symbol1 = atom1.getSymbol();
            String symbol2 = atom2.getSymbol();
            if (symbol1 == "O") {
                return -1;
            }
            if (symbol2 == "O") {
                return 1;
            }
            try {
                double weightA = AtomicProperties.getInstance().getMass(symbol1);
                double weightB = AtomicProperties.getInstance().getMass(symbol2);
                return (int)Math.signum(weightB - weightA);
            }
            catch (IOException e) {
                return 0;
            }
        }
    }

    private static Boolean isCarbon(IAtom atom) {
        return atom.getSymbol().equals("C");
    }
    
    // TODO (sorge) Eventually this needs to be rewritten to work with a list of
    // atoms, so it can be used for the rim of a fused ring as well.
    private Queue<IAtom> getInternalSubsts() {
        Queue<IAtom> result = new PriorityQueue<>(new InternalSubstComparator());
        for (IAtom atom : this.getStructure().atoms()) {
            if (!RichIsolatedRing.isCarbon(atom)) {
                result.add(atom);
            }
        }
        return result;
    }
}
