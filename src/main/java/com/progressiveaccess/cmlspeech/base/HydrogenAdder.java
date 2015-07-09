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
 * @file   HydrogenAdder.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Jul  9 14:32:07 2015
 *
 * @brief  Reattaches hydrogen atoms to the exploration structure.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.base;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.sre.SreAttribute;
import com.progressiveaccess.cmlspeech.sre.SreElement;
import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.SreUtil;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * Hydrogen adder for the original explicit hydrogens atoms and bonds.
 */
public final class HydrogenAdder {

  private Document doc;
  private SreElement annotation;
  private String atom;
  private String bond;
  private String hydrogen;


  /**
   * Private constructor for the hydrogen adder.
   *
   * @param document
   *          The CML document.
   * @param annotation
   *          The SRE annotation.
   */
  private HydrogenAdder(final Document document, final SreElement annotation) {
    this.doc = document;
    this.annotation = annotation;
    this.reattachHydrogens();
  }


  /**
   * Public method for calling the hydrogen adder.
   *
   * @param document
   *          The CML document.
   * @param annotation
   *          The SRE annotation.
   */
  public static void reattach(final Document document,
                              final SreElement annotation) {
    new HydrogenAdder(document, annotation);
  }


  /**
   * Finds and reattaches the originally explict hydrogenes.
   * It compares bonds in RichStructures with all those in the CML doc.
   * For each of the bonds it adds the bond and hydrogen atom as annotations,
   * and also both as components to the relevant annotations.
   */
  private void reattachHydrogens() {
    Set<String> atoms = RichStructureHelper.getAtoms().stream()
        .map(a -> a.getId()).collect(Collectors.toSet());
    Set<String> bonds = RichStructureHelper.getBonds().stream()
        .map(b -> b.getId()).collect(Collectors.toSet());
    Nodes nodes = SreUtil.xpathQuery(this.doc.getRootElement(), "//cml:bond");
    Integer size = 0;
    while (size < nodes.size()) {
      Element bondElement = (Element) nodes.get(size);
      size++;
      String bondId = bondElement.getAttributeValue("id");
      if (bonds.contains(bondId)) {
        continue;
      }
      String refs = bondElement.getAttributeValue("atomRefs2");
      String[] parts = refs.split(" ");
      if (parts.length < 2) {
        continue;
      }
      String atomA = parts[0];
      String atomB = parts[1];
      if (atoms.contains(atomA)) {
        if (atoms.contains(atomB)) {
          continue;
        }
        this.atom = atomA;
        this.hydrogen = atomB;
      } else {
        this.atom = atomB;
        this.hydrogen = atomA;
      }
      this.bond = bondId;
      Logger.logging("Adding hydrogen bond "
                     + this.bond + ":" + this.atom + "->" + this.hydrogen);
      this.insertHydrogenBond();
      this.addToContext();
      atoms.add(this.hydrogen);
      bonds.add(this.bond);
    }
  }


  /**
   * Inserts the hydrogen bond and atom at the relevant places.
   */
  private void insertHydrogenBond() {
    this.insertBond();
    Nodes nodes = SreUtil.xpathQuery(this.annotation,
        "//sre:annotation/sre:atom[.='" + atom + "']/..");
    Integer size = 0;
    while (size < nodes.size()) {
      SreElement atomElement = (SreElement) nodes.get(size);
      String parent = SreUtil.xpathValue(atomElement,
                                         "./sre:parents/sre:atomSet");
      this.addBond(atomElement);
      this.insertHydrogen(parent);
      size++;
    }
  }


  /**
   * Adds hydrogen and bond to all the contexts of the current atom.
   */
  private void addToContext() {
    Set<String> contexts =
        RichStructureHelper.getRichAtom(this.atom).getContexts();
    for (String context : contexts) {
      if (!RichStructureHelper.isAtomSet(context)) {
        continue;
      }
      SreElement node = (SreElement) SreUtil.xpathQueryElement(this.annotation,
          "//sre:annotation/sre:atomSet[.='" + context + "']/..");
      SreElement component = (SreElement) SreUtil.xpathQueryElement(node,
                                                    "./sre:component");
      component.appendChild(new SreElement(SreNamespace.Tag.BOND, this.bond));
      component.appendChild(new SreElement(SreNamespace.Tag.ATOM,
                                           this.hydrogen));
    }
  }


  /**
   * Adds hydrogen bond to other atom atom.
   *
   * @param atomElement
   *          The atom annotation element.
   */
  private void addBond(final SreElement atomElement) {
    SreElement component = (SreElement) SreUtil
        .xpathQueryElement(atomElement, "./sre:component");
    component.appendChild(new SreElement(SreNamespace.Tag.BOND, this.bond));
  }


  /**
   * Inserts the hydrogen bond.
   */
  private void insertBond() {
    SreElement element = new SreElement(SreNamespace.Tag.ANNOTATION);
    element.appendChild(new SreElement(SreNamespace.Tag.BOND, this.bond));
    element.appendChild(new SreElement(SreNamespace.Tag.PARENTS));
    element.appendChild(new SreElement(SreNamespace.Tag.POSITION, "1"));
    SreElement component = new SreElement(SreNamespace.Tag.COMPONENT);
    component.appendChild(new SreElement(SreNamespace.Tag.ATOM, this.hydrogen));
    component.appendChild(new SreElement(SreNamespace.Tag.ATOM, this.atom));
    element.appendChild(component);
    element.appendChild(new SreElement(SreNamespace.Tag.NEIGHBOURS));
    this.annotation.appendChild(element);
  }


  /**
   * Inserts the hydrogen atom.
   *
   * @param parent
   *          The parent element for the hydrogen atom annotation.
   */
  private void insertHydrogen(final String parent) {
    SreElement element = new SreElement(SreNamespace.Tag.ANNOTATION);
    SreElement atomElement = new SreElement(SreNamespace.Tag.ATOM,
                                            this.hydrogen);
    atomElement.addAttribute(new SreAttribute(SreNamespace.Attribute.TYPE,
                                              "Hydrogen"));
    element.appendChild(atomElement);
    SreElement parents = new SreElement(SreNamespace.Tag.PARENTS);
    parents.appendChild(new SreElement(SreNamespace.Tag.ATOMSET, parent));
    element.appendChild(parents);
    element.appendChild(new SreElement(SreNamespace.Tag.POSITION, "0"));
    SreElement component = new SreElement(SreNamespace.Tag.COMPONENT);
    component.appendChild(new SreElement(SreNamespace.Tag.BOND, this.bond));
    element.appendChild(component);
    element.appendChild(new SreElement(SreNamespace.Tag.NEIGHBOURS));
    this.annotation.appendChild(element);
  }

}
