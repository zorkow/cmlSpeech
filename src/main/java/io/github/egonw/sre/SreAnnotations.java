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
 * @file   SreAnnotations.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:21:38 2015
 * 
 * @brief  XML annotations structures.
 * 
 * 
 */

//

package io.github.egonw.sre;

import io.github.egonw.base.CmlNameComparator;

import nu.xom.Element;
import nu.xom.Nodes;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Basic class to add annotations like speech and structural representations to
 * CML objects.
 */

public class SreAnnotations extends SreElement {

  private SortedMap<String, Element> annotationNodes;

  SreAnnotations() {
    super(SreNamespace.Tag.ANNOTATIONS);
    this.annotationNodes = new TreeMap<>(new CmlNameComparator());
  }

  SreAnnotations(IAtomContainer molecule) {
    super(SreNamespace.Tag.ANNOTATIONS);
    for (IAtom atom : molecule.atoms()) {
      this.getNodeToAnnotate(atom.getID(), SreNamespace.Tag.ATOM);
    }
    for (IBond bond : molecule.bonds()) {
      this.getNodeToAnnotate(bond.getID(), SreNamespace.Tag.BOND);
    }
  }

  // Careful, this sets directly!
  public void registerAnnotation(String id, SreElement element) {
    this.annotationNodes.put(id, element);
  }

  public void registerAnnotation(String id, SreNamespace.Tag tag) {
    this.getNodeToAnnotate(id, tag);
  }

  public void registerAnnotation(String id, SreNamespace.Tag tag,
      SreAttribute attr) {
    Element element = this.getNodeToAnnotate(id, tag);
    element.addAttribute(attr);
  }

  public void appendAnnotation(String annotate, SreNamespace.Tag tag,
      Element entry) {
    this.appendAnnotation(
        this.getNodeToAnnotate(annotate, SreNamespace.Tag.UNKNOWN), tag, entry);
  }

  public void appendAnnotation(Element annotate, SreNamespace.Tag tag,
      Element entry) {
    Nodes nodes = SreUtil.xpathQuery(annotate, "//" + tag.tag);
    Element node = null;
    if (nodes.size() == 0) {
      node = new SreElement(tag);
      annotate.appendChild(node);
    } else {
      node = (Element) nodes.get(0);
    }
    node.appendChild(entry);
  }

  public void addAttribute(String id, SreAttribute attr) {
    Element element = this.getNodeToAnnotate(id);
    if (element == null) {
      throw new SreException("Annotation element " + id
          + " does not exist. Attribute cannot be added!");
    }
    element.addAttribute(attr);
  }

  private Element getNodeToAnnotate(String id) {
    return this.annotationNodes.get(id);
  }

  private Element getNodeToAnnotate(String id, SreNamespace.Tag tag) {
    Element element = this.getNodeToAnnotate(id);
    if (element != null) {
      return element;
    }
    Element annotation = new SreElement(SreNamespace.Tag.ANNOTATION);
    Element node = new SreElement(tag, id);
    annotation.appendChild(node);
    this.annotationNodes.put(id, annotation);
    return annotation;
  }

  public SreElement retrieveAnnotation(String id, SreNamespace.Tag tag) {
    Element element = this.annotationNodes.get(id);
    if (element == null) {
      return null;
    }
    return (SreElement) SreUtil.xpathQuery(element, "//" + tag.tag).get(0);
  }

  public void complete() {
    for (String key : this.annotationNodes.keySet()) {
      this.appendChild(this.annotationNodes.get(key));
    }
  }

  public String toString() {
    String result = "";
    for (String key : this.annotationNodes.keySet()) {
      result += key + ": " + this.annotationNodes.get(key).toXML() + "\n";
    }
    return result;
  }

}
