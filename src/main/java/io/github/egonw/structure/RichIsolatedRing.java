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
import org.openscience.cdk.tools.AtomicProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.connection.Connection;
import io.github.egonw.connection.ConnectionType;
import io.github.egonw.analysis.WeightComparator;
import org.openscience.cdk.interfaces.IBond;


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
        System.out.println(this.getExternalSubsts());
        List<IAtom> internalSubst = this.getInternalSubsts();
        if (internalSubst.size() > 1) {
            this.walkInternalSubst(internalSubst.get(0));
            return;
        }
        if (this.getConnectingAtoms().size() == 0) {
            if (internalSubst.size() == 1) {
                this.walkStraight(internalSubst.get(0));
                System.out.println(this.orderedAtomNames());
                return;
            }
            this.walkStraight(this.getStructure().getFirstAtom());
            System.out.println(this.orderedAtomNames());
            return;
        }
        //        startAtom ? this.enumerateInternalSystem.out.println(startAtom);
        this.walkStraight(this.getStructure().atoms().iterator().next());
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
    private List<IAtom> getInternalSubsts() {
        Queue<IAtom> substs = new PriorityQueue<>(new InternalSubstComparator());
        for (IAtom atom : this.getStructure().atoms()) {
            if (!RichIsolatedRing.isCarbon(atom)) {
                substs.add(atom);
            }
        }
        List<IAtom> result = new ArrayList<>();
        while (substs.peek() != null) {
            result.add(substs.poll());
        }
        return result;
    }


    // TODO (sorge) This comparator contains a lot of redundancy. This could be
    // simplified.
    private class ExternalSubstComparator implements Comparator<Connection> {
        private WeightComparator weightCompare = new WeightComparator();

        public int compare(Connection con1, Connection con2) {
            System.out.println(con1.getType());
            System.out.println(con2.getType());
            RichStructure<?> connected1 =
                RichStructureHelper.getRichStructure(con1.getConnected());
            RichStructure<?> connected2 =
                RichStructureHelper.getRichStructure(con2.getConnected());
            if (this.isHydroxylGroup(connected1)) {
                System.out.println(connected1.getId());
                return -1;
            }
            if (this.isHydroxylGroup(connected2)) {
                System.out.println(connected2.getId());
                return 1;
            }
            return weightCompare.compare(connected1, connected2);
        }

        private boolean isHydroxylGroup(RichStructure<?> structure) {
            if (!RichStructureHelper.isAtomSet(structure.getId())) {
                return false;
            }
            IAtomContainer container = ((RichAtomSet)structure).getStructure();
            if (container.getAtomCount() == 1) {
                return this.isHydroxyl(container.getAtom(0));
            }
            if (container.getAtomCount() == 2) {
                if (RichIsolatedRing.this.getComponents().contains(container.getAtom(0).getID())) {
                    return this.isHydroxyl(container.getAtom(1));
                }
                if (RichIsolatedRing.this.getComponents().contains(container.getAtom(1).getID())) {
                    return this.isHydroxyl(container.getAtom(0));
                }
                return false;
            }
            return false;
        }

        private boolean isHydroxyl(IAtom atom) {
            return atom.getSymbol() == "O" &&
                atom.getImplicitHydrogenCount() == 1;
        }
    }

    

    // TODO (sorge) Eventually this needs to be rewritten to work with a list of
    // atoms, so it can be used for the rim of a fused ring as well.
    private List<IAtom> getExternalSubsts() {
        Queue<Connection> connections = new PriorityQueue<>(new ExternalSubstComparator());
        System.out.println(this.getConnectingAtoms());
        System.out.println(this.getConnections());
        for (Connection connection : this.getConnections()) {
            ConnectionType type = connection.getType();
            if (type == ConnectionType.SPIROATOM ||
                type == ConnectionType.SHAREDATOM ||
                type == ConnectionType.CONNECTINGBOND) {
                connections.add(connection);
            }
        }
        List<IAtom> result = new ArrayList<>();
        while (connections.peek() != null) {
            Connection connection = connections.poll();
            ConnectionType type = connection.getType();
            if (type == ConnectionType.SPIROATOM ||
                type == ConnectionType.SHAREDATOM) {
                result.add
                    (RichStructureHelper.getRichAtom
                     (connection.getConnector()).getStructure());
            }
            if (type == ConnectionType.CONNECTINGBOND) {
                IBond bond = RichStructureHelper.getRichBond
                    (connection.getConnector()).getStructure();
                if (this.getComponents().contains(bond.getAtom(0).getID())) {
                    result.add(bond.getAtom(0));
                } else { result.add(bond.getAtom(1)); }
            }
        }
        return result;
    }
}
