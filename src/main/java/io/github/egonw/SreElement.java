
//
package io.github.egonw;

import nu.xom.Element;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtomSet;

/**
 *
 */

public class SreElement extends Element {

    SreElement(String tag) {
        super(SreNamespace.getInstance().prefix + ":" + tag,
              SreNamespace.getInstance().uri);
    }

    SreElement(SreNamespace.Tag tag) {
        super(tag.tag, SreNamespace.getInstance().uri);
    }

    SreElement(SreNamespace.Tag tag, String text) {
        super(tag.tag, SreNamespace.getInstance().uri);
        this.appendChild(text);
    }

    SreElement(IAtom obj) {
        this(SreNamespace.Tag.ATOM);
        this.appendChild(obj.getID());
    }

    SreElement(IBond obj) {
        this(SreNamespace.Tag.BOND);
        this.appendChild(obj.getID());
    }

    SreElement(CMLAtomSet obj) {
        this(SreNamespace.Tag.ATOMSET);
        this.appendChild(obj.getId());
    }

    SreElement(SreNamespace.Tag tag, Element child1, Element child2) {
        super(tag.tag, SreNamespace.getInstance().uri);
        this.appendChild(child1);
        this.appendChild(child2);
    }

}
