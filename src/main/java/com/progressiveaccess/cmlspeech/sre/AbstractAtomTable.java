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
 * @file   AbstractAtomTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Fri Jun 20 02:00:25 2014
 *
 * @brief  Abstract class for the localised atom tables.
 *
 *
 */

//
package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.structure.RichAtom;

import org.openscience.cdk.interfaces.IAtom;

import java.util.HashMap;

/**
 * Localised atom tables.
 */

public class AbstractAtomTable extends HashMap<String, String>
    implements AtomTable {

  private static final long serialVersionUID = 1L;

  @Override
  public String lookup(final String name) {
    final String result = this.get(name);
    if (result == null) {
      return "";
    }
    return result;
  }


  @Override
  public String lookup(final IAtom atom) {
    return lookup(atom.getSymbol());
  }


  @Override
  public String lookup(final RichAtom atom) {
    return lookup(atom.getStructure());
  }

}
