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
 * @file   RichAtomSet.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:19:38 2015
 * 
 * @brief  Rich atom set structures.
 * 
 * 
 */

//
package io.github.egonw.structure;

import io.github.egonw.base.CMLNameComparator;
import io.github.egonw.base.Logger;
import io.github.egonw.sre.SreUtil;

import com.google.common.base.Joiner;

import nu.xom.Document;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Base class for all atom sets with admin information.
 */

public abstract class RichAtomSet extends RichChemObject implements RichSet {
	
    public RichSetType type;
    public CMLAtomSet cml;

    public String iupac = "";
    public String name = "";
    public String molecularFormula = "";
    public String structuralFormula = "";
    
    private SortedSet<String> connectingAtoms = new TreeSet<String>(new CMLNameComparator());

    public ComponentsPositions componentPositions = new ComponentsPositions();
    public Integer offset = 0;

    public RichAtomSet (IAtomContainer container, String id, RichSetType type) {
        super(container);
        this.type = type;
        this.getStructure().setID(id);
        for (IAtom atom : this.getStructure().atoms()) {
            this.getComponents().add(atom.getID());
        }
        for (IBond bond : this.getStructure().bonds()) {
            this.getComponents().add(bond.getID());
        }
        this.makeCML();
    }


    @Override
    public IAtomContainer getStructure() {
        return (IAtomContainer)this.structure;
    }


    @Override
    public RichSetType getType() {
        return this.type;
    }
    

    @Override
    public SortedSet<String> getConnectingAtoms() {
        return this.connectingAtoms;
    }


    /**
     * Computes positions of atoms or substructures in the atom set.
     * We use the following heuristical preferences:
     * -- Always start with an element that has an external bond.
     * -- If multiple external elements we prefer one with an atom attached
     *    (or later with a functional group, as this can be voiced as substitution).
     * @param offset The position offset.
     * @param globalPositions Map of already assigned global positions.
     *
     */
    @Override
    public void computePositions(Integer offset) {
        this.offset = offset;
        this.walk();
    }


    /** 
     * Walks the structure and computes the positions of its elements.
     */
    protected abstract void walk();
    
    
    protected final List<IAtom> getSinglyConnectedAtoms() {
        List<IAtom> atoms = new ArrayList<>();
        for (IAtom atom : this.getStructure().atoms()) {
            if (this.getStructure().getConnectedAtomsList(atom).size() <= 1) {
                atoms.add(atom);
            }
        }
        return atoms;
    }


    protected final List<IAtom> getExternallyConnectedAtoms() {
        List<IAtom> atoms = new ArrayList<>();
        for (IAtom atom : this.getStructure().atoms()) {
            // FG: This is not working yet.
            // TODO sorge Do we need this? Or are these just the external connections.
            // It needs to be checked, wrt. external bonds as well.
            //
            if (this.connectingAtoms.contains(atom.getID())) {
                atoms.add(atom);
            }
        }
        return atoms;
    }

    
    @Override
    public void appendPositions(RichAtomSet atomSet) {
        this.componentPositions.putAll(atomSet.componentPositions);
    }


    protected final void walkStraight(IAtom atom) {
        this.walkStraight(atom, new ArrayList<IAtom>());
    }

    
    protected final void walkStraight(IAtom atom, List<IAtom> visited) {
        if (visited.contains(atom)) {
            return;
        }
        this.componentPositions.addNext(atom.getID());
        visited.add(atom);
        for (IAtom connected : this.getStructure().getConnectedAtomsList(atom)) {
            if (!visited.contains(connected)) {
                walkStraight(connected, visited);
                return;
            }
        }
    }


    /** 
     * Depth first traversal of structure.
     * 
     * @param atom The start atom.
     */
    protected final void walkDepthFirst(IAtom atom) {
        if (atom == null) {
            return;
        }
        List<IAtom> visited =  new ArrayList<IAtom>();
        Stack<IAtom> frontier = new Stack<IAtom>();
        frontier.push(atom);
        while (!frontier.empty()) {
            IAtom current = frontier.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            this.componentPositions.addNext(current.getID());
            this.getStructure().getConnectedAtomsList(current).stream().
                forEach(a -> frontier.push(a));
        }
    }


    @Override
    public String getAtom(Integer position) {
        return this.componentPositions.getAtom(position);
    }


    @Override
    public Integer getPosition(String atom) {
        return this.componentPositions.getPosition(atom);
    }
    

    @Override
    public Iterator<String> iterator() {
        return componentPositions.iterator();
    }


    public void printPositions () {
        Logger.logging(this.getId() + "\n" + componentPositions.toString());
    }


    public final List<String> orderedAtomNames() {
        if (this.componentPositions.size() == 0) {
            return null;
        }
        List<String> result = new ArrayList<>();
        while (result.size() < this.getStructure().getAtomCount()) result.add("");
        for (IAtom atom : this.getStructure().atoms()) {
            String id = atom.getID();
            result.set(this.getPosition(id) - 1, atom.getSymbol());
        }
        return result;
    }

        
    @Override
    public String toString() {
        String structure = super.toString();
        Joiner joiner = Joiner.on(" ");
        return structure +
            "\nSuper Systems:" + joiner.join(this.getSuperSystems()) +
            "\nSub Systems:" + joiner.join(this.getSubSystems()) +
            "\nConnecting Atoms:" + joiner.join(this.getConnectingAtoms());
    }
  

    private void makeCML() {
        this.cml = new CMLAtomSet();
        this.cml.setTitle(this.type.name);
        this.cml.setId(this.getId());
    }


    // This should only ever be called once!
    // Need a better solution!
    @Override
    public CMLAtomSet getCML(Document doc) {
        for (IAtom atom : this.getStructure().atoms()) { 
            String atomId = atom.getID();
            CMLAtom node = (CMLAtom)SreUtil.getElementById(doc, atomId);
            this.cml.addAtom(node);
        }
        return this.cml;
    }


    public static boolean isRing(RichAtomSet atomSet) {
        return atomSet.type == RichSetType.FUSED ||
            atomSet.type == RichSetType.ISOLATED ||
            atomSet.type == RichSetType.SMALLEST;
    }
}
