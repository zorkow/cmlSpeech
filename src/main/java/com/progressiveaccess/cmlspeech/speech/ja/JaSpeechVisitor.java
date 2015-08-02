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
 * @file   EnSpeechVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug  2 15:29:49 2015
 *
 * @brief  Abstract visitor for Japanese speech output.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.ja;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.speech.AbstractSpeechVisitor;
import com.progressiveaccess.cmlspeech.speech.Language;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;

import com.google.common.base.Joiner;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Basic visitor functionality for Japanese speech generation.
 */

public class JaSpeechVisitor extends AbstractSpeechVisitor {

  // TODO
  protected boolean subject = true;


  @Override
  public void visit(final RichBond bond) {
    this.addSpeech(Language.getBondTable().order(bond));
    this.addSpeech("結合"); // bond
  }


  @Override
  public void visit(final RichAtom atom) {
    Integer position = this.getContextPositions().getPosition(atom.getId());
    // TODO (sorge) Maybe take the supersystem of the atom outside the context.
    if (position == null) {
      this.describeSuperSystem(atom);
      return;
    }
    this.addSpeech(Language.getAtomTable().lookup(atom));
    this.addSpeech(position);
    if (this.subject) {
      this.addSpeech("は、"); // Separator (only after subject).
    }
    if (this.shortDescription) {
      return;
    }
    this.describeHydrogenBonds(atom);
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
    this.shortDescription = true;
    RichStructureHelper.getRichAtom(spiroAtom.getConnector()).accept(this);
    this.addSpeech("スピロ原子"); // spiro atom
    this.addSpeech("に"); // to
    RichStructureHelper.getRichAtomSet(spiroAtom.getConnected()).accept(this);
    this.addSpeech("、"); // Punctuation
    this.shortDescription = false;
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
    RichStructureHelper.getRichAtom(bridgeAtom.getConnector()).accept(this);
    this.addSpeech("橋頭原子");  // bridge atom
  }


  @Override
  public void visit(final ConnectingBond bond) {
    this.shortDescription = true;
    this.subject = false;
    String connected = bond.getConnected();
    if (RichStructureHelper.isAtom(connected)) {
      RichStructureHelper.getRichAtom(connected).accept(this);
    } else {
      RichStructureHelper.getRichAtomSet(connected).accept(this);
    }
    this.addSpeech("に"); // to
    RichStructureHelper.getRichBond(bond.getConnector()).accept(this);
    this.addSpeech("、"); // Punctuation
    // TODO (sorge) The past tense here is problematic!
    // this.modSpeech("して"); // ed (modifier)
    this.shortDescription = false;
    this.subject = true;
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.shortDescription = true;
    RichStructureHelper.getRichAtom(sharedAtom.getConnector()).accept(this);
    this.addSpeech("共有原子"); // shared atom
    RichStructureHelper.getRichAtomSet(sharedAtom.getConnected()).accept(this);
    this.remSpeech();
    this.addSpeech("含有"); // with
    this.addSpeech("、"); // Punctuation
    this.shortDescription = false;
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    RichStructureHelper.getRichBond(sharedBond.getConnector()).accept(this);
    this.addSpeech("共有"); // shared
  }


  @Override
  public void visit(final Bridge bridge) {
    this.shortDescription = true;
    RichStructureHelper.getRichAtomSet(bridge.getConnected()).accept(this);
    this.addSpeech("縮合");  // fused ??
    bridge.getBridges().forEach(c -> c.accept(this));
    this.addSpeech("に");  // at or via ??
    this.addSpeech("、"); // Punctuation
    this.shortDescription = false;
  }


  /**
   * Adds description of hydrogen bonds of an atom.
   *
   * @param atom
   *          The atom to describe.
   */
  private void describeHydrogenBonds(final RichAtom atom) {
    final Integer count = atom.getStructure().getImplicitHydrogenCount();
    switch (count) {
      case 0:
        return;
      case 1:
      default:
        this.addSpeech("水素");  // hydrogen (and hydrogens)
        this.addSpeech(count.toString());
        // this.addSpeech("に結合しており、"); // bonded to
        this.addSpeech("に結合、"); // bonded to
        return;
    }
  }


  // TODO (sorge) For the following utility functions, see if they can be
  // refactored with walk methods, etc.
  protected void describeSubstitutions(final RichAtomSet system) {
    final SortedSet<Integer> subst = new TreeSet<Integer>();
    for (final String atom : system.getConnectingAtoms()) {
      subst.add(system.getPosition(atom));
    }
    switch (subst.size()) {
      case 0:
        return;
      case 1:
      default:
        for (final Integer position : subst) {
          this.addSpeech(position);
          this.addSpeech("位"); // position
          this.addSpeech("と"); // and
        }
        this.remSpeech();
        this.addSpeech("で"); // at
        this.addSpeech("置換"); // Substitution
        this.addSpeech("、"); // Punctuation
        return;
    }
  }


  @Override
  public String getSpeech() {
    final Joiner joiner = Joiner.on("");
    String result = joiner.join(this.retrieveSpeech());
    this.clearSpeech();
    return result;
  }

}

// ring 環
// aliphatic chain 脂肪鎖
// fused ring system 縮合環系
// subring 部分環
// lsolated ring 孤立環
// functional group 官能基
// bridge atom 橋頭原子
// spiro atom スピロ原子
// shared atom 共有原子
// shared bond 共有結合
// bridge 橋
// bridged bond 橋状結合
// chain 直鎖
