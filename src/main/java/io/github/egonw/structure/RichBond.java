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
 * @file   RichBond.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jun 11 15:14:55 2014
 * 
 * @brief  Annotated Bond structure.
 * 
 * 
 */

//

package io.github.egonw.structure;

import io.github.egonw.sre.SreNamespace;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Bonds with admin informatoin.
 */

public class RichBond extends RichChemObject {

  public RichBond(IBond structure) {
    super(structure);

    for (IAtom atom : structure.atoms()) {
      this.getComponents().add(atom.getID());
    }
  }

  @Override
  public IBond getStructure() {
    return (IBond) this.structure;
  }

  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.BOND;
  }

}
