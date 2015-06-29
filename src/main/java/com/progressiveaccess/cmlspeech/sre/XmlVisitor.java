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
 * @file   XmlVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Apr 25 01:22:52 2015
 *
 * @brief  A simple visitor for XML annotations.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;

/**
 * XML visitor interface.
 */

public interface XmlVisitor {

  /**
   * Visits an atom.
   *
   * @param atom
   *          The visited atom.
   */
  default void visit(RichAtom atom) {
  }


  /**
   * Visits a bond.
   *
   * @param bond
   *          The visited bond.
   */
  default void visit(RichBond bond) {
  }


  /**
   * Visits a subring.
   *
   * @param subRing
   *          The visited subring.
   */
  default void visit(RichSubRing subRing) {
  }


  /**
   * Visits an isolated ring.
   *
   * @param isolatedRing
   *          The visited isolated ring.
   */
  default void visit(RichIsolatedRing isolatedRing) {
  }


  /**
   * Visits a fused ring.
   *
   * @param fusedRing
   *          The visited fused ring.
   */
  default void visit(RichFusedRing fusedRing) {
  }


  /**
   * Visits a functional group.
   *
   * @param functionalGroup
   *          The visited functional group.
   */
  default void visit(RichFunctionalGroup functionalGroup) {
  }


  /**
   * Visits an aliphatic chain.
   *
   * @param aliphaticChain
   *          The visited aliphatic chain.
   */
  default void visit(RichAliphaticChain aliphaticChain) {
  }


  /**
   * Visits a molecule.
   *
   * @param molecule
   *          The visited molecule.
   */
  default void visit(RichMolecule molecule) {
  }


  /**
   * Visits a spiro atom.
   *
   * @param spiroAtom
   *          The visited spiro atom.
   */
  default void visit(SpiroAtom spiroAtom) {
  }


  /**
   * Visits a bridge atom.
   *
   * @param bridgeAtom
   *          The visited bridge atom.
   */
  default void visit(BridgeAtom bridgeAtom) {
  }


  /**
   * Visits a shared atom.
   *
   * @param sharedAtom
   *          The visited shared atom.
   */
  default void visit(SharedAtom sharedAtom) {
  }


  /**
   * Visits a connecting bond.
   *
   * @param connectingBond
   *          The visited connecting bond.
   */
  default void visit(ConnectingBond connectingBond) {
  }


  /**
   * Visits a shared bond.
   *
   * @param sharedBond
   *          The visited shared bond.
   */
  default void visit(SharedBond sharedBond) {
  }


  /**
   * Visits a bridge.
   *
   * @param bridge
   *          The visited bridge.
   */
  default void visit(Bridge bridge) {
  }

}
