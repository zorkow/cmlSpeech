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
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:33:43 2015
 *
 * @brief  Utility class for Sre output.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility functions for the Sre annotations.
 */
public final class SreUtil {

  /** Dummy constructor. */
  private SreUtil() {
    throw new AssertionError("Instantiating utility class...");
  }


  public static Element getElementById(final Document doc, final String id) {
    final String query = "//*[@id='" + id + "']";
    final Nodes nodes = doc.query(query);
    return (Element) nodes.get(0);
  }

  public static Nodes xpathQuery(final Element element, final String query) {
    final XPathContext xc = XPathContext.makeNamespaceContext(element);
    xc.addNamespace(SreNamespace.getInstance().getPrefix(),
        SreNamespace.getInstance().getUri());
    xc.addNamespace("cml", "http://www.xml-cml.org/schema");
    return element.query(query, xc);
  }

  public static Node xpathQueryElement(final Element element,
                                       final String query) {
    Node node;
    try {
      node = xpathQuery(element, query).get(0);
    } catch (final IndexOutOfBoundsException e) {
      throw new SreException("Incorrect Xpath result!");
    }
    return node;
  }

  public static String xpathValue(final Element element, final String query) {
    final Nodes names = SreUtil.xpathQuery(element, query);
    if (names.size() != 0) {
      return names.get(0).getValue();
    }
    return "";
  }

  public static List<String> xpathValueList(final Element element,
      final String query) {
    final Nodes nodes = SreUtil.xpathQuery(element, query);
    final List<String> result = new ArrayList<String>();
    for (int i = 0; i < nodes.size(); i++) {
      result.add(nodes.get(i).getValue());
    }
    return result;
  }

  public static SreElement sreSet(final SreNamespace.Tag tag,
      final Collection<String> elements) {
    if (elements.isEmpty()) {
      return null;
    }
    final SreElement element = new SreElement(tag);
    elements.stream().forEach(e -> element.appendChild(SreUtil.sreElement(e)));
    return element;
  }

  public static SreElement sreElement(final String name) {
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
