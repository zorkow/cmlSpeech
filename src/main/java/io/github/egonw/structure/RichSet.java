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

package io.github.egonw.structure;

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
  public RichSetType getType();

  /**
   * @return The sorted set of connecting atoms.
   */
  public SortedSet<String> getConnectingAtoms();

  /**
   * @param doc
   *          The CML document structure.
   * 
   * @return A CML element represengin this atom set.
   */
  public CMLAtomSet getCml(Document doc);

  /**
   * Finds an element in the set by its position.
   * 
   * @param position
   *          The position of the element to retrieve.
   * 
   * @return The name of the retrieved element.
   */
  public String getAtom(Integer position);

  /**
   * Retrieves the position of an element in the set.
   * 
   * @param element
   *          The name of the element.
   * 
   * @return The retrieved position.
   */
  public Integer getPosition(String element);
}
