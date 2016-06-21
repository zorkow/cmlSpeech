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
 * @file AbstractBondTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date Sun Aug 2 12:58:22 2015
 *
 * @brief Abstract class for the localised bond tables.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.structure.RichBond;

import org.openscience.cdk.interfaces.IBond;

import java.util.HashMap;

/**
 * Localised bond tables.
 */

public class AbstractBondTable extends HashMap<String, String>
    implements BondTable {

  private static final long serialVersionUID = 1L;

  @Override
  public String order(final String name) {
    final String result = this.get(name);
    if (result == null) {
      return "";
    }
    return result;
  }


  @Override
  public String order(final IBond bond) {
    return this.order(bond.getOrder().toString().toLowerCase());
  }


  @Override
  public String order(final RichBond bond) {
    return this.order(bond.getStructure());
  }


  @Override
  public String stereo(final String name) {
    final String result = this.get(name);
    if (result == null) {
      return "";
    }
    return result;
  }


  @Override
  public String stereo(final IBond bond) {
    return this.order(bond.getStereo().toString().toLowerCase());
  }


  @Override
  public String stereo(final RichBond bond) {
    return this.order(bond.getStructure());
  }

}
