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
 * @file   SreOutput.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Jun 19 16:34:40 2014
 *
 * @brief  Class to handle SRE annotations.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichStructure;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.AbstractRichStructure;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;


/**
 * Constructs structural annotations for Sre.
 */
public class AnnotationVisitor implements XmlVisitor {

  private SreAnnotations annotations = new SreAnnotations();
  private SreElement element;


  public SreAnnotations getAnnotations() {
    return this.annotations;
  }


  @Override
  public void visit(RichAtom atom) {
    this.structureAnnotation(atom);
  }
  

  @Override
  public void visit(RichBond bond) {
    this.structureAnnotation(bond);
  }
  

  @Override
  public void visit(RichSubRing subRing) {
    this.setAnnotation(subRing);
  }


  @Override
  public void visit(RichIsolatedRing isolatedRing) {
    this.setAnnotation(isolatedRing);
  }


  @Override
  public void visit(RichFusedRing fusedRing) {
    this.setAnnotation(fusedRing);
  }


  @Override
  public void visit(RichFunctionalGroup functionalGroup) {
    this.setAnnotation(functionalGroup);
  }


  @Override
  public void visit(RichAliphaticChain aliphaticChain) {
    this.setAnnotation(aliphaticChain);
  }


  @Override
  public void visit(RichMolecule molecule) {
    this.setAnnotation(molecule);
  }


  @Override
  public void visit(SpiroAtom spiroAtom) {
    this.connectionAnnotation(spiroAtom,
        SreNamespace.Tag.ATOM, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(BridgeAtom bridgeAtom) {
    this.connectionAnnotation(bridgeAtom,
        SreNamespace.Tag.ATOM, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(SharedAtom sharedAtom) {
    this.connectionAnnotation(sharedAtom,
        SreNamespace.Tag.ATOM, SreNamespace.Tag.ATOMSET);
  }


  @Override
  public void visit(ConnectingBond connectingBond) {
    final String connected = connectingBond.getConnected();
    final SreNamespace.Tag type = RichStructureHelper.isAtom(connected)
        ? SreNamespace.Tag.ATOM
        : SreNamespace.Tag.ATOMSET;
    this.connectionAnnotation(connectingBond, SreNamespace.Tag.BOND, type);
  }


  @Override
  public void visit(SharedBond sharedBond) {
    this.connectionAnnotation(sharedBond,
        SreNamespace.Tag.BOND, SreNamespace.Tag.ATOMSET); 
  }


  private void setAnnotation(RichAtomSet set) {
    this.structureAnnotation(set);
    this.atomSetAnnotation(set);
  }

  private void structureAnnotation(AbstractRichStructure<?> structure) {
    this.element = new SreElement(SreNamespace.Tag.ANNOTATION);
    this.annotations.registerAnnotation(structure.getId(), this.element);
    this.element.appendChild(new SreElement(structure.tag(), structure.getId()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.CONTEXT,
        structure.getContexts()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.COMPONENT,
        structure.getComponents()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.EXTERNALBONDS,
        structure.getExternalBonds()));
    this.connectionsAnnotations(structure);
  }

  
  private void atomSetAnnotation(RichAtomSet set) {
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
   * Computes annotations for structure's connections.
   *
   * @return The annotation element.
   */
  private void connectionsAnnotations(AbstractRichStructure<?> structure) {
    if (structure.getConnections().isEmpty()) {
      return;
    }
    final SreElement oldElement = this.element;
    this.element = new SreElement(SreNamespace.Tag.CONNECTIONS);
    structure.getConnections().stream().forEach(c -> c.accept(this));
    oldElement.appendChild(this.element);
    this.element = oldElement;
  }


  public void connectionAnnotation(Connection connection,
      SreNamespace.Tag connector, SreNamespace.Tag connected) {
    this.element.appendChild(
        new SreElement(connection.tag(),
                       new SreElement(connector, connection.getConnector()),
                       new SreElement(connected, connection.getConnected())));
  }

}
