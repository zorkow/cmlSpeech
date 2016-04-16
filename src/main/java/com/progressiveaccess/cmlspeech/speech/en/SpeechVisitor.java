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
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug  2 15:29:49 2015
 *
 * @brief  Abstract visitor for English speech output.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.en;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.speech.AbstractSpeechVisitor;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Basic visitor functionality for English speech generation.
 */

@SuppressWarnings("serial")
public abstract class SpeechVisitor extends AbstractSpeechVisitor {

  @Override
  public void visit(final RichBond bond) {
    this.addName(bond);
    this.push("bond");
  }


  @Override
  public void visit(final RichAtom atom) {
    Integer position = this.getContextPositions().getPosition(atom.getId());
    // TODO (sorge) Maybe take the supersystem of the atom outside the context.
    if (position == null) {
      this.describeSuperSystem(atom);
      return;
    }
    this.addName(atom);
    this.push(position);
    if (this.getFlag("short")) {
      return;
    }
    this.describeHydrogenBonds(atom);
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
    this.setFlag("short", true);
    this.push("spiro atom");
    RichStructureHelper.getRichAtom(spiroAtom.getConnector()).accept(this);
    this.push("to");
    RichStructureHelper.getRichAtomSet(spiroAtom.getConnected()).accept(this);
    this.setFlag("short", false);
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
    this.push("bridge atom");
    RichStructureHelper.getRichAtom(bridgeAtom.getConnector()).accept(this);
  }


  @Override
  public void visit(final ConnectingBond bond) {
    this.setFlag("short", true);
    RichStructureHelper.getRichBond(bond.getConnector()).accept(this);
    // TODO (sorge) The past tense here is problematic!
    this.modLast("ed");
    this.push("to");
    String connected = bond.getConnected();
    if (RichStructureHelper.isAtom(connected)) {
      RichStructureHelper.getRichAtom(connected).accept(this);
    } else {
      RichStructureHelper.getRichAtomSet(connected).accept(this);
    }
    this.setFlag("short", false);
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.setFlag("short", true);
    this.push("shared atom");
    RichStructureHelper.getRichAtom(sharedAtom.getConnector()).accept(this);
    this.push("with");
    RichStructureHelper.getRichAtomSet(sharedAtom.getConnected()).accept(this);
    this.setFlag("short", false);
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    this.push("shared");
    RichStructureHelper.getRichBond(sharedBond.getConnector()).accept(this);
  }


  @Override
  public void visit(final Bridge bridge) {
    this.setFlag("short", true);
    this.push("fused with");
    RichStructureHelper.getRichAtomSet(bridge.getConnected()).accept(this);
    this.push("via");
    bridge.getBridges().forEach(c -> c.accept(this));
    this.setFlag("short", false);
  }


  @Override
  protected void describeHydrogenBonds(final RichAtom atom) {
    final Integer count = atom.getStructure().getImplicitHydrogenCount();
    switch (count) {
      case 0:
        return;
      case 1:
        this.push("bonded to");
        this.push(count.toString());
        this.push("hydrogen");
        return;
      default:
        this.push("bonded to");
        this.push(count.toString());
        this.push("hydrogens");
        return;
    }
  }


  @Override
  protected void describeSubstitutions(final RichAtomSet system) {
    final SortedSet<Integer> subst = new TreeSet<Integer>();
    for (final String atom : system.getConnectingAtoms()) {
      subst.add(system.getPosition(atom));
    }
    switch (subst.size()) {
      case 0:
        return;
      case 1:
        this.push("Substitution at position");
        this.push(subst.iterator().next());
        return;
      default:
        this.push("Substitutions at positions");
        for (final Integer position : subst) {
          this.push(position);
          this.push("and");
        }
        this.pop();
    }
  }

}