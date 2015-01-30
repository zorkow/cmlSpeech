//
package io.github.egonw;

import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.AtomContainer;
import java.util.List;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import java.util.SortedSet;
import java.util.TreeSet;
import nu.xom.Document;
import nu.xom.Element;
import org.xmlcml.cml.element.CMLAtom;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.naming.OperationNotSupportedException;
import java.util.Stack;

/**
 *
 */

public class RichAtomSet extends RichChemObject implements Iterable<String> {
	
    public enum Type {
        ALIPHATIC ("Aliphatic chain"),
        FUSED ("Fused ring"),
        ISOLATED ("Isolated ring"),
        SMALLEST ("Subring"),
        MOLECULE ("Molecule"),
        FUNCGROUP ("Functional Group"),
        ;

        protected final String name;

        private Type (String name) {
            this.name = name;
        }
    }

    public Type type;
    public CMLAtomSet cml;

    public String iupac = "";
    public String name = "";
    public String molecularFormula = "";
    public String structuralFormula = "";
    
    private SortedSet<String> connectingAtoms = new TreeSet<String>(new CMLNameComparator());

    public ComponentsPositions componentPositions = new ComponentsPositions();
    public Integer offset = 0;

    // To remove!
    public Set<IAtom> atomConnections = new HashSet<IAtom>();
    public Set<IAtom> setConnections = new HashSet<IAtom>();


    private RichAtomSet (IAtomContainer container) {
        super(container);
    }

    private RichAtomSet (IAtomContainer container, Type type) {
        super(container);
        this.type = type;
    }

    public RichAtomSet (IAtomContainer container, Type type, String id) {
        super(container);
        this.getStructure().setID(id);
        this.type = type;
        for (IAtom atom : this.getStructure().atoms()) {
            this.getComponents().add(atom.getID());
        }
        for (IBond bond : this.getStructure().bonds()) {
            this.getComponents().add(bond.getID());
        }

        this.makeCML();
    }


    public Type getType() {
        return this.type;
    }
    

    public SortedSet<String> getConnectingAtoms() {
        return this.connectingAtoms;
    }


    @Override
    public IAtomContainer getStructure() {
        return (IAtomContainer)this.structure;
    }

    
    private void makeCML() {
        this.cml = new CMLAtomSet();
        this.cml.setTitle(this.type.name);
        this.cml.setId(this.getId());
    }


    public void addConnection(IAtom atom, RichAtomSet set, IBond bond) {
        this.setConnections.add(atom);
    }

    public void addConnection(IAtom atom, IAtom extAtom, IBond bond) {
        this.atomConnections.add(atom);
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
    public void computePositions(Integer offset) {
        this.offset = offset;
        switch (this.type) {
        case FUSED:
            throw new SreException("Illegal position computation for ring systems!");
        case FUNCGROUP:
            computeAtomPositionsFuncGroup();
            break;
        case ALIPHATIC:
            computeAtomPositionsAliphatic();
            break;
        case SMALLEST:
        case ISOLATED:
        default:
            computeAtomPositionsIsolated();
        }
    }

    
    public void appendPositions(RichAtomSet atomSet) {
        this.componentPositions.putAll(atomSet.componentPositions);
    }


    private IAtom getSinglyConnectedAtom() {
        for (IAtom atom : this.getStructure().atoms()) {
            if (this.getStructure().getConnectedAtomsList(atom).size() <= 1) {
                return atom;
            }
        }
        return null;
    }


    private IAtom getExternallyConnectedAtom() {
        for (IAtom atom : this.getStructure().atoms()) {
            // FG: This is not working yet.
            //
            // It needs to be checked, wrt. external bonds as well.
            //
            if (this.connectingAtoms.contains(atom.getID())) {
                return atom;
            }
        }
        return null;
    }

    
    private void computeAtomPositionsFuncGroup() {
        IAtom startAtom = getExternallyConnectedAtom();
        if (startAtom == null) {
            startAtom = getSinglyConnectedAtom();
        }
        if (startAtom == null) {
            throw new SreException("Aliphatic chain without start atom!");
        }
        this.walkGroup(startAtom);
    }


    // Walk functional group depth first starting at atom.
    private void walkGroup(IAtom atom) {
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

    
    private void computeAtomPositionsAliphatic() {
        IAtom startAtom = getSinglyConnectedAtom();
        if (startAtom == null) {
            throw new SreException("Aliphatic chain without start atom!");
        }
        this.walkRing(startAtom, new ArrayList<IAtom>());
    }

    private void computeAtomPositionsIsolated() {
        IAtom startAtom;
        if (this.atomConnections.size() == 0 && setConnections.size() == 0) {
            List<IAtom> atoms = Lists.newArrayList(this.getStructure().atoms());
            startAtom = atoms.get(0);
        } else if (this.atomConnections.size() == 0) {
            startAtom = this.setConnections.iterator().next();
        } else {
            startAtom = this.atomConnections.iterator().next();
        }
        this.walkRing(startAtom, new ArrayList<IAtom>());
    }

    private void walkRing(IAtom atom, List<IAtom> visited) {
        if (visited.contains(atom)) {
            return;
        }
        this.componentPositions.addNext(atom.getID());
        visited.add(atom);
        for (IAtom connected : this.getStructure().getConnectedAtomsList(atom)) {
            if (!visited.contains(connected)) {
                walkRing(connected, visited);
                return;
            }
        }
    }


    public String getAtom(Integer position) {
        return this.componentPositions.getAtom(position);
    }


    public Integer getPosition(String atom) {
        return this.componentPositions.getPosition(atom);
    }
    

    public Iterator<String> iterator() {
        return componentPositions.iterator();
    }


    public void printPositions () {
        Logger.logging(this.getId() + "\n" + componentPositions.toString());
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
  

    // This should only ever be called once!
    // Need a better solution!
    public CMLAtomSet getCML(Document doc) {
        for (IAtom atom : this.getStructure().atoms()) { 
            String atomId = atom.getID();
            CMLAtom node = (CMLAtom)SreUtil.getElementById(doc, atomId);
            this.cml.addAtom(node);
        }
        return this.cml;
    }

    public CMLAtomSet getCML() {
        return this.cml;
    }
}
