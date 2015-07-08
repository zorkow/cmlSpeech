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
 * @file SreOutput.java
 * @author Volker Sorge <a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date Thu Jun 19 16:34:40 2014
 *
 * @brief Class to handle SRE annotations.
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
import com.progressiveaccess.cmlspeech.structure.RichChemObject;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;


/**
 * Constructs structural annotations for structures.
 */
public class AnnotationVisitor implements XmlVisitor {

  private final SreAnnotations annotations = new SreAnnotations();
  private SreElement element;


  /**
   * @return The annotation the visitor computes.
   */
  public SreAnnotations getAnnotations() {
    return this.annotations;
  }


  @Override
  public void visit(final RichAtom atom) {
    this.structureAnnotation(atom);
  }


  @Override
  public void visit(final RichBond bond) {
    this.structureAnnotation(bond);
  }


  @Override
  public void visit(final RichSubRing subRing) {
    this.setAnnotation(subRing);
  }


  @Override
  public void visit(final RichIsolatedRing isolatedRing) {
    this.setAnnotation(isolatedRing);
  }


  @Override
  public void visit(final RichFusedRing fusedRing) {
    this.setAnnotation(fusedRing);
  }


  @Override
  public void visit(final RichFunctionalGroup functionalGroup) {
    this.setAnnotation(functionalGroup);
  }


  @Override
  public void visit(final RichAliphaticChain aliphaticChain) {
    this.setAnnotation(aliphaticChain);
  }


  @Override
  public void visit(final RichMolecule molecule) {
    this.setAnnotation(molecule);
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
    this.connectionAnnotation(spiroAtom,
        SreNamespace.Tag.ATOM, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
    this.connectionAnnotation(bridgeAtom,
        SreNamespace.Tag.ATOM, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.connectionAnnotation(sharedAtom,
        SreNamespace.Tag.ATOM, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(final ConnectingBond connectingBond) {
    final SreElement connection = new SreElement(connectingBond.tag());
    connection.appendChild(new SreElement(SreNamespace.Tag.BOND,
                                          connectingBond.getConnector()));
    connection.appendChild(SreUtil.sreElement(connectingBond.getConnected()));
    connection.appendChild(new SreElement(SreNamespace.Tag.ATOM,
                                          connectingBond.getOrigin()));
    this.element.appendChild(connection);
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    this.connectionAnnotation(sharedBond,
        SreNamespace.Tag.BOND, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(final Bridge bridge) {
    final SreElement oldElement = this.element;
    this.element = new SreElement(SreNamespace.Tag.BRIDGE);
    this.element.appendChild(SreUtil.sreElement(bridge.getConnected()));
    bridge.getBridges().stream().forEach(b -> b.accept(this));
    oldElement.appendChild(this.element);
    this.element = oldElement;
  }


  /**
   * Computes annotations of a set.
   *
   * @param set
   *          The rich atom set.
   */
  private void setAnnotation(final RichAtomSet set) {
    this.structureAnnotation(set);
    this.atomSetAnnotation(set);
  }


  /**
   * Computes annotations for a structure.
   *
   * @param structure
   *          The rich structure.
   */
  private void structureAnnotation(final RichChemObject structure) {
    this.element = new SreElement(SreNamespace.Tag.ANNOTATION);
    this.annotations.registerAnnotation(structure.getId(), this.element);
    this.element
        .appendChild(new SreElement(structure.tag(), structure.getId()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.CONTEXT,
        structure.getContexts()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.COMPONENT,
        structure.getComponents()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.EXTERNALBONDS,
        structure.getExternalBonds()));
    this.connectionsAnnotations(structure);
  }


  /**
   * Computes annotations special for an atom set.
   *
   * @param set
   *          The rich atom set.
   */
  private void atomSetAnnotation(final RichAtomSet set) {
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.INTERNALBONDS,
        set.getInternalBonds()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.SUBSYSTEM,
        set.getSubSystems()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.SUPERSYSTEM,
        set.getSuperSystems()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.CONNECTINGATOMS,
        set.getConnectingAtoms()));
  }


  /**
   * Computes annotations for a structure's connections.
   *
   * @param structure
   *          The structure that is currently visited.
   */
  private void connectionsAnnotations(final RichChemObject structure) {
    if (structure.getConnections().isEmpty()) {
      return;
    }
    final SreElement oldElement = this.element;
    this.element = new SreElement(SreNamespace.Tag.CONNECTIONS);
    structure.getConnections().stream().forEach(c -> c.accept(this));
    oldElement.appendChild(this.element);
    this.element = oldElement;
  }


  /**
   * Append annotations for a single connection to the current annotation
   * element.
   *
   * @param connection
   *          The connection.
   * @param connector
   *          The tag of the connector.
   * @param connected
   *          The tag of the connected.
   */
  public void connectionAnnotation(final Connection connection,
      final SreNamespace.Tag connector, final SreNamespace.Tag connected) {
    this.element.appendChild(
        new SreElement(connection.tag(),
            new SreElement(connector, connection.getConnector()),
            new SreElement(connected, connection.getConnected())));
  }

}
