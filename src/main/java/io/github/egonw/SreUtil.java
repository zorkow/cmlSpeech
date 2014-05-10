
//
package io.github.egonw;

import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Nodes;
import nu.xom.Document;

/**
 *
 */

public class SreUtil {

    public static void appendAttribute (Element element, SreAttribute attr) {
        String localName = attr.getLocalName();
        String namespace = attr.getNamespaceURI();
        SreAttribute oldAttr = (SreAttribute)element.getAttribute(localName, namespace);
        if (oldAttr == null) {
            element.addAttribute(attr);
        } else {
            oldAttr.addValue(attr.getValue());
        }
    };

    public static void appendAttribute(Element element, String localName, String value) {
        String namespace = SreNamespace.getInstance().uri;
        SreAttribute oldAttr = (SreAttribute)element.getAttribute(localName, namespace);
        if (oldAttr == null) {
            element.addAttribute(new SreAttribute(localName, value));
        } else {
            oldAttr.addValue(value);
        }
    };

    public static Element getElementById(Document doc, String id) {
        String query = "//*[@id='" + id + "']";
        Nodes nodes = doc.query(query);
        return (Element)nodes.get(0);
    }


    public static void appendNode(Element element, String tag, String value) {
        Element sreElement = new SreElement(tag);
        sreElement.appendChild(value);
        element.appendChild(sreElement);
    };
}
