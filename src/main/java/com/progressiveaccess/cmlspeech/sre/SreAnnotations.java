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

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.base.CmlNameComparator;

import nu.xom.Element;
import nu.xom.Nodes;

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

  // Careful, this sets directly!
  public void registerAnnotation(final String id, final SreElement element) {
    this.annotationNodes.put(id, element);
  }

  public void registerAnnotation(final String id, final SreNamespace.Tag tag) {
    this.getNodeToAnnotate(id, tag);
  }

  public void registerAnnotation(final String id, final SreNamespace.Tag tag,
      final SreAttribute attr) {
    final Element element = this.getNodeToAnnotate(id, tag);
    element.addAttribute(attr);
  }

  public void appendAnnotation(final String annotate,
      final SreNamespace.Tag tag,
      final Element entry) {
    this.appendAnnotation(
        this.getNodeToAnnotate(annotate, SreNamespace.Tag.UNKNOWN), tag, entry);
  }

  public void appendAnnotation(final String annotate,
      final SreNamespace.Tag tag,
      final String entry) {
    final Element element = this.getNodeToAnnotate(annotate);
    element.appendChild(new SreElement(tag, entry));
  }

  public void appendAnnotation(final Element annotate,
      final SreNamespace.Tag tag,
      final Element entry) {
    final Nodes nodes = SreUtil.xpathQuery(annotate, "//" + tag.getTag());
    Element node = null;
    if (nodes.size() == 0) {
      node = new SreElement(tag);
      annotate.appendChild(node);
    } else {
      node = (Element) nodes.get(0);
    }
    node.appendChild(entry);
  }

  public void addAttribute(final String id, final SreAttribute attr) {
    final Element element = this.getNodeToAnnotate(id);
    if (element == null) {
      throw new SreException("Annotation element " + id
          + " does not exist. Attribute cannot be added!");
    }
    element.addAttribute(attr);
  }

  private Element getNodeToAnnotate(final String id) {
    return this.annotationNodes.get(id);
  }

  private Element getNodeToAnnotate(final String id,
                                    final SreNamespace.Tag tag) {
    final Element element = this.getNodeToAnnotate(id);
    if (element != null) {
      return element;
    }
    final Element annotation = new SreElement(SreNamespace.Tag.ANNOTATION);
    final Element node = new SreElement(tag, id);
    annotation.appendChild(node);
    this.annotationNodes.put(id, annotation);
    return annotation;
  }

  public SreElement retrieveAnnotation(final String id,
      final SreNamespace.Tag tag) {
    final Element element = this.annotationNodes.get(id);
    if (element == null) {
      return null;
    }
    return (SreElement) SreUtil.xpathQuery(element, "//" + tag.getTag()).get(0);
  }

  public void complete() {
    for (final String key : this.annotationNodes.keySet()) {
      this.appendChild(this.annotationNodes.get(key));
    }
  }

  @Override
  public String toString() {
    String result = "";
    for (final String key : this.annotationNodes.keySet()) {
      result += key + ": " + this.annotationNodes.get(key).toXML() + "\n";
    }
    return result;
  }

}
