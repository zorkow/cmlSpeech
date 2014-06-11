
//
package io.github.egonw;

import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.AtomContainer;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 *
 */

public class RichAtomSet extends RichChemObject {
    
    public enum Type {
        ALIPHATIC ("Aliphatic chain"),
        FUSED ("Fused ring"),
        ISOLATED ("Isolated ring"),
        SMALLEST ("Subring");

        protected final String name;

        private Type (String name) {
            this.name = name;
        }
    }

    public Type type;
    public CMLAtomSet cml;

    public Set<String> sup = new HashSet<String>();
    public Set<String> sub = new HashSet<String>();
 
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
        this.cml = new CMLAtomSet();
        this.cml.setTitle(this.type.name);
        this.cml.setId(this.getId());
    }


    public void addSub(String sub) {
        this.sub.add(sub);
    }

    public void addSubs(List<String> subs) {
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

    @Override
    public IAtomContainer getStructure() {
        return (IAtomContainer)this.structure;
    }

    
    public CMLAtomSet getCML() {
        return cml;
    }

}
