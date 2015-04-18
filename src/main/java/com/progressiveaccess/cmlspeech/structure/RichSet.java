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
 * @file   RichSet.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Feb 24 14:26:35 2015
 *
 * @brief  Interface for enriched atom sets.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

import nu.xom.Document;

import org.openscience.cdk.interfaces.IChemObject;
import org.xmlcml.cml.element.CMLAtomSet;

import java.util.SortedSet;

/**
 * Interface for all rich sets, i.e. molecules, rings, chains and groups.
 */

public interface RichSet extends RichStructure<IChemObject>, Iterable<String> {

  /**
   * @return The type of this rich atom set.
   */
  RichSetType getType();

  /**
   * @return The sorted set of connecting atoms.
   */
  SortedSet<String> getConnectingAtoms();

  /**
   * Finalises the CML representation for this atom set.
   * This should only be called once!
   *
   * @param doc
   *          The CML document structure.
   *
   * @return A CML element representing this atom set.
   */
  CMLAtomSet getCml(Document doc);

  /**
   * Finds an element in the set by its position.
   *
   * @param position
   *          The position of the element to retrieve.
   *
   * @return The name of the retrieved element.
   */
  String getAtom(Integer position);

  /**
   * Retrieves the position of an element in the set.
   *
   * @param element
   *          The name of the element.
   *
   * @return The retrieved position.
   */
  Integer getPosition(String element);

  /**
   * Sets the molecular formula of the set.
   *
   * @param formula
   *          The molecular formula.
   */
  void setMolecularFormula(String formula);

  /**
   * @return The molecular formular of the set.
   */
  String getMolecularFormula();
}
