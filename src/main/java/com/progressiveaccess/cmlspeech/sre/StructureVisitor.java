// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file   StructureVisitor.java
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date   Sat Apr 25 23:36:58 2015
 * 
 * @brief  Visitor to construct the exploration structure.
 * 
 * 
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.google.common.collect.TreeMultimap;
import java.util.Comparator;
import com.progressiveaccess.cmlspeech.base.CmlNameComparator;

/**
 * Constructs the exploration structure.
 */

public class StructureVisitor implements XmlVisitor {

  //private final SreAnnotations annotations = new SreAnnotations();

  private TreeMultimap<String, SreElement> annotations = TreeMultimap.create(new CmlNameComparator(), new SreComparator());
  private SreElement element;


  private class SreComparator implements Comparator<SreElement> {

  @Override
  public int compare(final SreElement element1, final SreElement element2) {
    return 1;
  }}

  
  /** 
   * @return The annotation the visitor computes.
   */
  public SreElement getAnnotations() {
    SreElement element = new SreElement(SreNamespace.Tag.ANNOTATIONS);
    for (final String key : this.annotations.keySet()) {
      for (final SreElement value : this.annotations.get(key)) {
        element.appendChild(value);
      }
    }
    return element;
  }


  @Override
  public void visit(final RichAtom atom) {
    for (String parent : atom.getSuperSystems()) {
      this.element = new SreElement(SreNamespace.Tag.ANNOTATION);
      annotations.put(atom.getId(), this.element);
      // this.annotations.registerAnnotation(atom.getId(),
      //                                     this.element);
      this.atomStructure(atom, RichStructureHelper.getRichAtomSet(parent));
    }
  }


  /**
   * Computes annotations for a structure.
   *
   * @param structure
   *          The rich structure.
   */
  private void atomStructure(final RichAtom atom, final RichAtomSet parent) {
    this.element
        .appendChild(new SreElement(atom.tag(), atom.getId()));
    this.element.appendChild(new SreElement(SreNamespace.Tag.PARENTS,
        parent.getId()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.COMPONENT,
        atom.getComponents()));
    this.element.appendChild(new SreElement(SreNamespace.Tag.POSITION, 
        parent.getPosition(atom.getId()).toString()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.CHILDREN,
        atom.getSubSystems()));
                             //this.connectionsAnnotations(atom);
  }


  public void complete() {
  }
  
}
