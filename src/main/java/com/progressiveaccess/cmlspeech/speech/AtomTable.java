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
 * @file AtomTable.java
 * @author Volker Sorge <a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date Fri Jun 20 02:00:25 2014
 *
 * @brief Singleton class to translate atom names.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.structure.RichAtom;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Maps atom identifiers to their proper names.
 */

public interface AtomTable {

  /**
   * Gets the name of an atom given its chemical symbol.
   *
   * @param name
   *          The atom symbol.
   *
   * @return The atom name.
   */
  String lookup(final String name);


  /**
   * Gets the name of an atom.
   *
   * @param atom
   *          The atom.
   *
   * @return The atom name.
   */
  String lookup(final IAtom atom);


  /**
   * Gets the name of an rich atom.
   *
   * @param atom
   *          The rich atom.
   *
   * @return The atom name.
   */
  String lookup(final RichAtom atom);

}
