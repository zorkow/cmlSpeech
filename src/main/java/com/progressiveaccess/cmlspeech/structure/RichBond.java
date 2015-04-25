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

package com.progressiveaccess.cmlspeech.structure;

import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.XmlVisitor;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Bonds with admin informatoin.
 */
public class RichBond extends RichChemObject {

  /**
   * Generates the rich bond structure.
   *
   * @param structure
   *          The chemical bond.
   */
  public RichBond(final IBond structure) {
    super(structure);
    for (final IAtom atom : structure.atoms()) {
      this.getComponents().add(atom.getID());
    }
  }


  @Override
  public IBond getStructure() {
    return (IBond) super.getStructure();
  }


  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.BOND;
  }


  @Override
  public void accept(final XmlVisitor visitor) {
    visitor.visit(this);
  }

}
