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
 * @file   TypeVisitor.java
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *          Sorge</a>
 * @date   Tue Jun 23 00:08:44 2015
 *
 * @brief  A simple visitor for type attribute information.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;

/**
 * Computes type annotations for XML attributes.
 */

public class TypeVisitor implements XmlVisitor {

  private String type = "";


  /**
   * @return The computed type.
   */
  public String getType() {
    return type;
  }


  @Override
  public void visit(final RichAtom atom) {
    this.type = AtomTable.lookup(atom);
  }


  @Override
  public void visit(final RichBond bond) {
    this.type = bond.getStructure().getOrder().toString().toLowerCase();
  }


  @Override
  public void visit(final RichIsolatedRing ring) {
    this.atomSetType(ring);
  }


  @Override
  public void visit(final RichFusedRing ring) {
    this.atomSetType(ring);
  }


  @Override
  public void visit(final RichSubRing ring) {
    this.atomSetType(ring);
  }


  @Override
  public void visit(final RichAliphaticChain chain) {
    this.atomSetType(chain);
  }


  @Override
  public void visit(final RichFunctionalGroup group) {
    this.atomSetType(group);
  }


  @Override
  public void visit(final RichMolecule molecule) {
    this.atomSetType(molecule);
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
    this.connectionType(spiroAtom);
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
    this.connectionType(bridgeAtom);
  }


  @Override
  public void visit(final ConnectingBond bond) {
    this.connectionType(bond);
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.connectionType(sharedAtom);
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    this.connectionType(sharedBond);
  }


  @Override
  public void visit(final Bridge bridge) {
    this.connectionType(bridge);
  }


  /**
   * Compute the type of a connection.
   *
   * @param connection
   *          The connection.
   */
  private void connectionType(final Connection connection) {
    this.type = connection.getType().getName();
  }


  /**
   * Compute the type of an atom set.
   *
   * @param structure
   *          The atom set.
   */
  private void atomSetType(final RichAtomSet structure) {
    this.type = structure.getType().getName();
  }

}
