
//
package io.github.egonw;

import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.AtomContainer;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import nu.xom.Element;
import org.openscience.cdk.interfaces.IAtom;
import com.google.common.collect.Lists;
import org.openscience.cdk.interfaces.IBond;
import java.util.ArrayList;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 *
 */

public class RichAtomSet extends CMLAtomSet {
    
    public enum Type {
        ALIPHATIC,
        FUSED,
        ISOLATED,
        SMALLEST;

        private Type () {
        }
    }

    public IAtomContainer container;
    public Type type;
    public Set<String> sup = new HashSet<String>();
    public Set<String> sub = new HashSet<String>();
    public Set<IAtom> atomConnections = new HashSet<IAtom>();
    public Set<IAtom> setConnections = new HashSet<IAtom>();
    public BiMap<Integer, String> elementPositions = HashBiMap.create();


    public RichAtomSet (IAtomContainer container) {
        super();
        this.container = container;
    }

    public RichAtomSet (IAtomContainer container, Type type) {
        super();
        this.container = container;
        this.type = type;
    }

    public void addSub(String sub) {
        this.sub.add(sub);
    }

    public void addSub(List<String> subs) {
        this.sub.addAll(subs);
    }

    public void addSup(String sup) {
        this.sup.add(sup);
    }

    public void addSups(List<String> sups) {
        this.sup.addAll(sups);
    }


    public boolean isSub(String atomSet) {
        return this.sub.stream().anyMatch(as -> as == atomSet);
    };

    public boolean isSub(RichAtomSet atomSet) {
        return this.isSub(atomSet.getId());
    };


    public boolean isSup(String atomSet) {
        return this.sup.stream().anyMatch(as -> as == atomSet);
    };

    public boolean isSup(RichAtomSet atomSet) {
        return this.isSup(atomSet.getId());
    };


    public Set<String> siblings(List<RichAtomSet> atomSets) {
        Set<String> result = new HashSet<String>();
        if (this.type == RichAtomSet.Type.SMALLEST) {
            for (String atomSet : this.sup) {
                result.addAll((retrieveAtomSet(atomSets, atomSet)).sub);
            }
        }
        result.remove(this.getId());
        return result;
    }

    
    /**
     * Retrieves an enriched atom set by its name.
     * @param atomSets List of atom sets to search.
     * @param name Name of atom set to retrieve. 
     * @return Atomset.
     */
    public static RichAtomSet retrieveAtomSet(List<RichAtomSet> atomSets, String name) {
        return atomSets.stream()
            .filter(as -> name == as.getId())
            .findFirst()
            .get();
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
     * @param annotations Annotations for the atom set.
     *          
     */
    public void computePositions() {
        switch (this.type) {
        case FUSED:
            computeSubstructurePositions();
            break;
        case ALIPHATIC:
            computeAtomPositionsAliphatic();
            break;
        case SMALLEST:
        case ISOLATED:
        default:
            computeAtomPositionsIsolated();
        }
        printConnections();
    }


    private void computeAtomPositionsAliphatic() {
        IAtom startAtom = null;
        for (IAtom atom : this.container.atoms()) {
            if (this.container.getConnectedAtomsList(atom).size() == 1) {
                startAtom = atom;
                if (this.atomConnections.contains(startAtom)) {
                    return;
                }
            }
        }
        if (startAtom == null) {
            throw new SreException("Aliphatic chain without start atom!");
        }
        this.walkRing(startAtom, 1, new ArrayList<IAtom>());
    }

    private void computeSubstructurePositions() {
        // Not yet implemented...
    }


    private void computeAtomPositionsIsolated() {
        IAtom startAtom;
        if (this.atomConnections.size() == 0 && setConnections.size() == 0) {
            List<IAtom> atoms = Lists.newArrayList(this.container.atoms());
            startAtom = atoms.get(0);
        } else if (this.atomConnections.size() == 0) {
            startAtom = this.setConnections.iterator().next();
        } else {
            startAtom = this.atomConnections.iterator().next();
        }
        this.walkRing(startAtom, 1, new ArrayList<IAtom>());
    }

    private void walkRing(IAtom atom, Integer count, List<IAtom> visited) {
        if (visited.contains(atom)) {
            return;
        }
        this.elementPositions.put(count, atom.getID());
        visited.add(atom);
        for (IAtom connected : this.container.getConnectedAtomsList(atom)) {
            if (!visited.contains(connected)) {
                walkRing(connected, ++count, visited);
                return;
            }
        } 
    }


    public String getPositionAtom(Integer position) {
        return this.elementPositions.get(position);
    }


    public Integer getAtomPosition(String atom) {
        return this.elementPositions.inverse().get(atom);
    }


    private void printConnections () {
        for (Integer key : this.elementPositions.keySet()) {
            System.out.printf("%d: %s\n", key, this.elementPositions.get(key));
        }
    }

}
