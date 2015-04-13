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
 * @file   RichStructure.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jun 10 21:37:18 2014
 *
 * @brief  Interface specification for enriched structures.
 *
 *
 */

//

package io.github.egonw.structure;

import io.github.egonw.connection.Connection;

import java.util.SortedSet;

/**
 * Interface for all structures with admin information.
 */

public interface RichStructure<S> {

  /**
   * @return The list of components of this structure.
   */
  SortedSet<String> getComponents();

  /**
   * @return The list of contexts of this structure.
   */
  SortedSet<String> getContexts();

  /**
   * @return The list of external bonds of this structure.
   */
  SortedSet<String> getExternalBonds();

  /**
   * @return The list of connections of this structure.
   */
  SortedSet<Connection> getConnections();

  /**
   * @return The list of direct Super-Systems.
   */
  SortedSet<String> getSuperSystems();

  /**
   * @return The list of direct Sub-Systems. These can also be atoms.
   */
  SortedSet<String> getSubSystems();

  /**
   * Returns the ID of the structure.
   *
   * @return ID string.
   */
  String getId();

  /**
   * The structure embedded in this enriched object.
   *
   * @return Un-enriched structure.
   */
  S getStructure();

}
