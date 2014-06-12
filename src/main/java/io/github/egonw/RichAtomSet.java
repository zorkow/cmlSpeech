
//
package io.github.egonw;

import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.AtomContainer;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

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

    private Set<String> sup = new HashSet<String>();
    private Set<String> sub = new HashSet<String>();
 
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

        for (IAtom atom : this.getStructure().atoms()) {
            this.getComponents().add(atom.getID());
        }
        for (IBond bond : this.getStructure().bonds()) {
            this.getComponents().add(bond.getID());
        }
    }


    public Set<String> getSub() {
        return this.sub;
    }


    public Set<String> getSup() {
        return this.sup;
    }


    // Refactor some of these functions!
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
        return this.getComponents().contains(atomSet);
    };

    public boolean isSub(RichAtomSet atomSet) {
        return this.isSub(atomSet.getId());
    };


    public boolean isSup(String atomSet) {
        return this.getContexts().contains(atomSet);
    };

    public boolean isSup(RichAtomSet atomSet) {
        return this.isSup(atomSet.getId());
    };


    @Override
    public IAtomContainer getStructure() {
        return (IAtomContainer)this.structure;
    }

    
    public CMLAtomSet getCML() {
        return cml;
    }

}
