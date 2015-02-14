/**
 * @file   SreElement.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:31:09 2015
 * 
 * @brief  Sre Elements
 * 
 * 
 */

//
package io.github.egonw.sre;

import nu.xom.Element;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Basic elements for Sre annotations.
 * @extends Element
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

    SreElement(IAtomContainer obj) {
        this(SreNamespace.Tag.ATOMSET);
        this.appendChild(obj.getID());
    }

    SreElement(SreNamespace.Tag tag, Element child1, Element child2) {
        super(tag.tag, SreNamespace.getInstance().uri);
        this.appendChild(child1);
        this.appendChild(child2);
    }

}
