// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


/**
 * @file   SreUtil.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:33:43 2015
 * 
 * @brief  Utility class for Sre output.
 * 
 * 
 */

//
package io.github.egonw.sre;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtomSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions for the Sre annotations.
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


    public Element createSreAnnotations() {
        return new SreElement(SreNamespace.Tag.ANNOTATIONS);
    }

    public Element createSreAnnotation(Node node) {
        Element element = new SreElement(SreNamespace.Tag.ANNOTATION);
        element.appendChild(node);
        return element;
    }

    public Element createSreAnnotation(IAtom atom) {
        return createSreAnnotation(createSreObject(atom));
    }

    public Element createSreAnnotation(IBond bond) {
        return createSreAnnotation(createSreObject(bond));
    }

    public Element createSreAnnotation(CMLAtomSet atomSet) {
        return createSreAnnotation(createSreObject(atomSet));
    }

    public Element createSreObject (IAtom obj) {
        Element element = new SreElement(SreNamespace.Tag.ATOM);
        element.appendChild(obj.getID());
        return element;
    }

    public Element createSreObject (IBond obj) {
        Element element = new SreElement(SreNamespace.Tag.BOND);
        element.appendChild(obj.getID());
        return element;
    }

    public Element createSreObject (CMLAtomSet obj) {
        Element element = new SreElement(SreNamespace.Tag.ATOMSET);
        element.appendChild(obj.getId());
        return element;
    }

    public static Nodes xpathQuery(Element element, String query) {
        XPathContext xc = XPathContext.makeNamespaceContext(element);
        xc.addNamespace(SreNamespace.getInstance().prefix, SreNamespace.getInstance().uri);
        xc.addNamespace("cml", "http://www.xml-cml.org/schema");
        return element.query(query, xc);
    }


    public static Node xpathQueryElement(Element element, String query) {
        Node node;
        try {
            node = xpathQuery(element, query).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new SreException("Incorrect Xpath result!");
        }
        return node;
    }


    public static String xpathValue(Element element, String query) {
        Nodes names = SreUtil.xpathQuery(element, query);
        if (names.size() != 0) {
            return names.get(0).getValue();
        }
        return "";
    }

    public static List<String> xpathValueList(Element element, String query) {
        Nodes nodes = SreUtil.xpathQuery(element, query);
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++) {
            result.add(nodes.get(i).getValue());
        }
        return result;
    }

    
}
