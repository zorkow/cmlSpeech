/**
 * @file   SreAttribute.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:29:44 2015
 * 
 * @brief  Attributes for speech annotations.
 * 
 * 
 */

//
package io.github.egonw.sre;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Attribute structure.
 * @extends Attribute
 */

public class SreAttribute extends Attribute {
 
    public SreAttribute(String localName, String value) {
        super(SreNamespace.getInstance().prefix + ":" + localName,
              SreNamespace.getInstance().uri, value);
    }

    public SreAttribute(SreNamespace.Attribute attr, String value) {
        super(SreNamespace.getInstance().prefix + ":" + attr.attribute,
              SreNamespace.getInstance().uri, value);
    }

    public void addValue(String value) {
        if (getValue() == "") {
            setValue(value);
        } else {
            setValue(getValue() + " " + value);
        }
    }

    public void addValue(Element node) {
        String localName = getLocalName();
        String namespace = getNamespaceURI();
        SreAttribute oldAttr = (SreAttribute)node.getAttribute(localName, namespace);
        if (oldAttr == null) {
            node.addAttribute(this);
        } else {
            oldAttr.setValue(oldAttr.getValue() + " " + getValue());
        }
    }

}
