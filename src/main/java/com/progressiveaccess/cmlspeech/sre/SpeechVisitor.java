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
 * @file   SpeechVisitor.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Tue Jun 30 14:46:54 2015
 * 
 * @brief  Simple speech visitor.
 * 
 * 
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.connection.ConnectionType;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichChemObject;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces the basic speech for structures.
 */

public class SpeechVisitor implements XmlVisitor {

  private RichAtomSet context = null;
  private List<String> speech = new ArrayList<String>();
  private Integer id = 0;
  
  @Override
  public void visit(final RichAtom atom) {
    for (final String parent : atom.getSuperSystems()) {
      this.context = RichStructureHelper.getRichAtomSet(parent);
    }
  }


  @Override
  public void visit(final RichIsolatedRing ring) {
  }


  @Override
  public void visit(final RichFusedRing ring) {
  }


  @Override
  public void visit(final RichSubRing ring) {
  }


  @Override
  public void visit(final RichAliphaticChain chain) {
  }


  @Override
  public void visit(final RichFunctionalGroup group) {
  }


  @Override
  public void visit(final RichMolecule molecule) {
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
  }


  @Override
  public void visit(final ConnectingBond bond) {
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
  }


  @Override
  public void visit(final SharedBond sharedBond) {
  }


  @Override
  public void visit(final Bridge bridge) {
  }
  

  private void addSpeech(String msg) {
    this.speech.add(msg);
  }

}
