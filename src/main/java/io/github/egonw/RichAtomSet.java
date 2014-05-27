
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

    public Set<String> siblings(List<RichAtomSet> atomSets) {
        Set<String> result = new HashSet<String>();
        if (this.type == RichAtomSet.Type.SMALLEST) {
            for (String atomSet : this.sup) {
                result.addAll(sub);
            }
        }
        return result;
    }
}
