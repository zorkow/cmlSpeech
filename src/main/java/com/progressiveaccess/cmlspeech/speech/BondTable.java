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
 * @file   BondTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug  2 12:58:22 2015
 *
 * @brief  Singleton class to translate bond names.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.structure.RichBond;

import org.openscience.cdk.interfaces.IBond;

/**
 * Maps bond identifiers to their proper names.
 */

public interface BondTable {

  /**
   * Gets the name of an bond given its chemical symbol.
   *
   * @param name
   *          The bond symbol.
   *
   * @return The bond name.
   */
  String order(final String name);


  /**
   * Gets the name of an bond.
   *
   * @param bond
   *          The bond.
   *
   * @return The bond name.
   */
   String order(final IBond bond);


  /**
   * Gets the name of an rich bond.
   *
   * @param bond
   *          The rich bond.
   *
   * @return The bond name.
   */
   String order(final RichBond bond);

  
  /**
   * Gets the name of an bond given its chemical symbol.
   *
   * @param name
   *          The bond symbol.
   *
   * @return The bond name.
   */
  String stereo(final String name);


  /**
   * Gets the name of an bond.
   *
   * @param bond
   *          The bond.
   *
   * @return The bond name.
   */
   String stereo(final IBond bond);


  /**
   * Gets the name of an rich bond.
   *
   * @param bond
   *          The rich bond.
   *
   * @return The bond name.
   */
   String stereo(final RichBond bond);

}
