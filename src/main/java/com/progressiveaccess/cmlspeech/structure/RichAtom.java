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
 * @file   RichAtom.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jun 11 15:14:55 2014
 *
 * @brief  Annotated Atom structure.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

import com.progressiveaccess.cmlspeech.sre.AtomTable;
import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.XmlVisitor;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Atoms with admin information.
 */
public class RichAtom extends RichChemObject {

  /**
   * Generates the rich atom structure.
   *
   * @param structure
   *          The chemical atom.
   */
  public RichAtom(final IAtom structure) {
    super(structure);
    this.setName(AtomTable.lookup(this));
  }


  @Override
  public IAtom getStructure() {
    return (IAtom) super.getStructure();
  }


  /**
   * @return True if atom is carbon.
   */
  public Boolean isCarbon() {
    return this.getStructure().getSymbol().equals("C");
  }


  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.ATOM;
  }


  @Override
  public void accept(final XmlVisitor visitor) {
    visitor.visit(this);
  }


  // TODO (sorge) Refactor into speech visitor.
  @Override
  public String shortSimpleDescription() {
    return this.getName();
  }


  /**
   * Computes the atom description in the context of an atom set.
   *
   * @param atomSet
   *          A rich atom set.
   *
   * @return The speech string for the atom.
   */
  public String shortSimpleDescription(final RichAtomSet atomSet) {
    return this.getName() + this.describeAtomPosition(atomSet);
  }


  @Override
  public String longSimpleDescription() {
    return this.getName() + this.describeHydrogenBonds();
  }


  /**
   * Computes the atom description in the context of an atom set.
   *
   * @param atomSet
   *          A rich atom set.
   *
   * @return The speech string for the atom.
   */
  public String longSimpleDescription(final RichAtomSet atomSet) {
    return this.getName() + this.describeAtomPosition(atomSet)
        + this.describeHydrogenBonds();
  }


  /**
   * @return Description of hydrogen bonds of the atom.
   */
  private String describeHydrogenBonds() {
    final Integer count = this.getStructure().getImplicitHydrogenCount();
    switch (count) {
      case 0:
        return "";
      case 1:
        return " bonded to " + count.toString() + " hydrogen";
      default:
        return " bonded to " + count.toString() + " hydrogens";
    }
  }


  /**
   * Describes the position of the atom in the context of a given atom set.
   *
   * @param atomSet
   *          A rich atom set.
   *
   * @return The speech string for the atom's position.
   */
  private String describeAtomPosition(final RichAtomSet atomSet) {
    final Integer position = atomSet.getPosition(this.getId());
    return position == null ? "" : " " + position.toString();
  }

}
