
//
package io.github.egonw;

import io.github.egonw.SreAttribute;
import io.github.egonw.SreNamespace;
import nu.xom.Element;
import nu.xom.Attribute;

/**
 *
 */

public class SreElement extends Element {

    SreElement(String tag) {
        super(SreNamespace.getInstance().prefix + ":" + tag,
              SreNamespace.getInstance().uri);
    }
    
    public void appendAttribute (SreAttribute attr) {
        String localName = attr.getLocalName();
        String namespace = attr.getNamespaceURI();
        SreAttribute oldAttr = (SreAttribute)getAttribute(localName, namespace);
        if (oldAttr == null) {
            addAttribute(attr);
        } else {
            oldAttr.addValue(attr.getValue());
        }
    };

    public void appendAttribute(String localName, String value) {
        String namespace = SreNamespace.getInstance().uri;
        SreAttribute oldAttr = (SreAttribute)getAttribute(localName, namespace);
        if (oldAttr == null) {
            addAttribute(new SreAttribute(localName, value));
        } else {
            oldAttr.addValue(value);
        }
    };

}
