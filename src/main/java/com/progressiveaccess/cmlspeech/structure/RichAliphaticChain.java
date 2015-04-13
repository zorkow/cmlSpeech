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
 * @file   RichAliphaticChain.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Feb 24 17:13:29 2015
 *
 * @brief  Implementation of rich aliphatic chain.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

import com.progressiveaccess.cmlspeech.sre.SreException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.List;

/**
 * Atom sets that are rich aliphatic chains.
 */

public class RichAliphaticChain extends RichAtomSet {

  public RichAliphaticChain(final IAtomContainer container, final String id) {
    super(container, id, RichSetType.ALIPHATIC);
  }

  @Override
  public RichSetType getType() {
    return this.type;
  }

  protected final List<IAtom> getSinglyConnectedAtoms() {
    final List<IAtom> atoms = new ArrayList<>();
    for (final IAtom atom : this.getStructure().atoms()) {
      if (this.getConnectedAtomsList(atom).size() <= 1) {
        atoms.add(atom);
      }
    }
    return atoms;
  }

  @Override
  protected final void walk() {
    final List<IAtom> atoms = this.getSinglyConnectedAtoms();
    // Here we assume that the list is only of length 2.
    // Otherwise something very peculiar is wrong!
    if (atoms.size() != 2) {
      throw new SreException("Aliphatic chain without two ends!");
    }
    final IAtom startAtom = this.findLowestSubstitution(atoms.get(0),
        atoms.get(1));
    this.walkStraight(startAtom);
  }

  private final List<IAtom> visited = new ArrayList<>();

  /**
   * Finds the lowest external or internal substitution. Walks chain from either
   * side, returns the start atom for the lowest external substitution if
   * possible. If this is in the middle or does not exist, prefers the lowest
   * internal substitution.
   *
   * @param leftAtom
   *          One end of the chain.
   * @param rightAtom
   *          The other end of the chain.
   *
   * @return atom with lowest substitution.
   */
  private IAtom findLowestSubstitution(final IAtom leftAtom,
      final IAtom rightAtom) {
    final IAtomContainer structure = this.getStructure();
    IAtom currentLeft = leftAtom;
    IAtom currentRight = rightAtom;
    Integer internal = 0;
    Integer external = 0;
    Integer pointer = 0;
    final Integer middle = (int) Math.ceil(structure.getAtomCount() / 2d);
    while (pointer < middle && external == 0) {
      pointer++;
      if (this.getConnectingAtoms().contains(currentLeft.getID())) {
        external = -1 * pointer;
        break;
      }
      if (this.getConnectingAtoms().contains(currentRight.getID())) {
        external = pointer;
        break;
      }
      final IAtom nextLeft = this.chooseNext(currentLeft);
      final IAtom nextRight = this.chooseNext(currentRight);
      // TODO sorge Add stereo chemistry here!
      if (internal == 0) {
        if (structure.getBond(currentLeft, nextLeft).getOrder()
            != IBond.Order.SINGLE) {
          internal = -1 * pointer;
        }
        if (structure.getBond(currentRight, nextRight).getOrder()
            != IBond.Order.SINGLE) {
          internal = pointer;
        }
      }
      currentLeft = nextLeft;
      currentRight = nextRight;
    }
    if (external == 0
        || (structure.getAtomCount() % 2 == 1 && Math.abs(external)
            == middle)) {
      if (internal > 0) {
        return rightAtom;
      }
      return leftAtom;
    }
    if (external > 0) {
      return rightAtom;
    }
    return leftAtom;
  }

  /**
   * Finds the next atom in the chain that has not yet been visited.
   *
   * @param atom
   *          The source atom.
   *
   * @return atom that is next to the input atom.
   */
  private IAtom chooseNext(final IAtom atom) {
    this.visited.add(atom);
    final List<IAtom> connected = this.getStructure().getConnectedAtomsList(
        atom);
    if (!this.visited.contains(connected.get(0))) {
      return connected.get(0);
    }
    if (this.visited.size() > 1 && !this.visited.contains(connected.get(1))) {
      return connected.get(1);
    }
    return null;
  }

}
