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
import io.github.egonw.analysis.RichStructureHelper;
import java.util.Collection;

/**
 * Utility functions for the Sre annotations.
 */

public class SreUtil {

    public static Element getElementById(Document doc, String id) {
        String query = "//*[@id='" + id + "']";
        Nodes nodes = doc.query(query);
        return (Element)nodes.get(0);
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

    public static SreElement sreSet (SreNamespace.Tag tag, Collection<String> elements) {
        if (elements.isEmpty()) {
            return null;
        }
        SreElement element = new SreElement(tag);
        elements.stream().forEach(e -> element.appendChild(SreUtil.sreElement(e)));
        return element;
    }
    
    public static SreElement sreElement(String name) {
        if (RichStructureHelper.isAtom(name)) {
            return new SreElement(SreNamespace.Tag.ATOM, name);
        }
        if (RichStructureHelper.isBond(name)) {
            return new SreElement(SreNamespace.Tag.BOND, name);
        }
        if (RichStructureHelper.isAtomSet(name)) {
            return new SreElement(SreNamespace.Tag.ATOMSET, name);
        }
        return new SreElement(SreNamespace.Tag.UNKNOWN, name);
    }
}
