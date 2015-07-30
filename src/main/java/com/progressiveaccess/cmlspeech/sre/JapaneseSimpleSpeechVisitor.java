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
 * @file   JapaneseSimpleSpeechVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jul  8 09:07:00 2015
 *
 *a @brief  Visitor for simple speech descriptions.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichChemObject;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSetType;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;

import com.google.common.base.Joiner;

import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Produces the simple speech for structures.
 */

public class JapaneseSimpleSpeechVisitor implements SpeechVisitor {

  private ComponentsPositions contextPositions = null;
  private LinkedList<String> speech = new LinkedList<String>();
  private boolean shortDescription = false;
  private boolean subject = true;
  private static Map<String, String> bondMap =
      new HashMap<String, String>();

  static {
    bondMap.put("single", "単");
    bondMap.put("double", "二重");
    bondMap.put("triple", "三重");
    bondMap.put("quadruple", "四重");
  }


  public void setContextPositions(final ComponentsPositions positions) {
    this.contextPositions = positions;
  }


  @Override
  public void visit(final RichBond bond) {
    this.addSpeech(bondMap.get(bond.orderDescription()));
    this.addSpeech("結合"); // bond
  }


  @Override
  public void visit(final RichAtom atom) {
    Integer position = this.contextPositions.getPosition(atom.getId());
    // TODO (sorge) Maybe take the supersystem of the atom outside the context.
    if (position == null) {
      this.describeSuperSystem(atom);
      return;
    }
    this.addSpeech(atom.getName()); // Done below
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
  public void visit(final RichIsolatedRing ring) {
    this.addSpeech(ring.getComponentsPositions().size());
    this.addSpeech("員"); // Elements
    this.addSpeech("環"); // Ring
    this.addSpeech("、"); // Punctuation
    if (this.shortDescription) {
      return;
    }
    this.describeReplacements(ring);
    this.describeMultiBonds(ring);
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichFusedRing ring) {
    this.addSpeech("縮合環系");  // Fused ring system
    this.addSpeech("、"); // Punctuation
    if (this.shortDescription) {
      return;
    }
    this.addSpeech(ring.getSubSystems().size());
    this.addSpeech("個の");
    this.addSpeech("部分環"); // subrings
    this.addSpeech("を"); 
    this.addSpeech("含有"); // with 
    this.addSpeech("、"); // Punctuation
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichSubRing ring) {
    this.addSpeech(ring.getComponentsPositions().size());
    this.addSpeech("員"); // Elements
    this.addSpeech("部分環"); // Subring
    this.addSpeech("、"); // Punctuation
    if (this.shortDescription) {
      return;
    }
    this.describeReplacements(ring);
    this.describeMultiBonds(ring);
  }


  @Override
  public void visit(final RichAliphaticChain chain) {
    // this.addSpeech("脂肪鎖"); // Aliphatic chain
    this.addSpeech("長さ"); // length
    this.addSpeech(chain.getComponentsPositions().size());
    this.addSpeech("の"); // of
    this.addSpeech("直鎖"); // Chain
    this.addSpeech("、"); // Punctuation
    if (this.shortDescription) {
      return;
    }
    this.describeReplacements(chain);
    this.describeMultiBonds(chain);
    this.describeSubstitutions(chain);
  }


  @Override
  public void visit(final RichFunctionalGroup group) {
    this.addSpeech("官能基");
    this.addSpeech(group.getStructuralFormula());
    this.addSpeech("、"); // Punctuation
  }


  @Override
  public void visit(final RichMolecule molecule) {
    this.shortDescription = true;
    Integer i = 0;
    for (String set : molecule.getPath()) {
      ((RichChemObject)
       RichStructureHelper.getRichStructure(set)).accept(this);
      i++;
      if (i == 1) {
        this.speech.removeLast();
        this.addSpeech("と、"); // and Punctuation
      }
    }
    this.speech.removeLast();
    this.addSpeech("で構成された分子");  // Molecule consisting of
    this.addSpeech("、"); // Punctuation
    this.shortDescription = false;
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
    this.addSpeech("fused with");
    RichStructureHelper.getRichAtomSet(bridge.getConnected()).accept(this);
    this.addSpeech("via");
    bridge.getBridges().forEach(c -> c.accept(this));
    this.shortDescription = false;
  }


  private void modSpeech(final String msg) {
    String last = this.speech.removeLast();
    this.speech.offerLast(last + msg);
  }


  private void addSpeech(final String msg) {
    if (!msg.equals("")) {
      this.speech.add(msg);
    }
  }


  private void addSpeech(final Integer num) {
    this.addSpeech(num.toString());
  }


  public String getSpeech() {
    if (this.speech.size() == 0) {
      return "";
    }
    final Joiner joiner = Joiner.on("");
    String result = joiner.join(this.speech);
    this.speech.clear();
    return result;
  }


  // TODO (sorge) For the following utility functions, see if they can be
  // refactored with walk methods, etc.
  private void describeReplacements(final RichAtomSet system) {
    final Iterator<String> iterator = system.iterator();
    while (iterator.hasNext()) {
      final String value = iterator.next();
      final RichAtom atom = RichStructureHelper.getRichAtom(value);
      if (!atom.isCarbon()) {
        this.addSpeech(system.getPosition(value));
        this.addSpeech("位"); // Position symbol
        this.addSpeech("は"); // at 
        this.addSpeech(atom.getName());
        this.addSpeech("、"); // Punctuation
      }
    }
  }


  //TODO (sorge) Sort those bonds. Maybe combine with a more stateful walk.
  private void describeMultiBonds(final RichAtomSet system) {
    for (final String component : system.getComponents()) {
      if (!RichStructureHelper.isBond(component)) {
        continue;
      }
      RichBond bond = RichStructureHelper.getRichBond(component);
      if (bond.isSingle()) {
        continue;
      }
      Integer atomA = system.getPosition(bond.getComponents().first());
      Integer atomB = system.getPosition(bond.getComponents().last());
      if (atomA > atomB) {
        atomA ^= atomB;
        atomB ^= atomA;
        atomA ^= atomB;
      }
      this.addSpeech(atomA);
      this.addSpeech("位"); // Position symbol
      this.addSpeech("と"); // and
      this.addSpeech(atomB);
      this.addSpeech("位"); // Position symbol
      this.addSpeech("の間は"); // between
      bond.accept(this);
      this.addSpeech("、"); // Punctuation
    }
  }


  private void describeSubstitutions(final RichAtomSet system) {
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
        this.speech.removeLast();
        this.addSpeech("で"); // at
        this.addSpeech("置換"); // Substitution
        this.addSpeech("、"); // Punctuation
        return;
    }
  }


  /** 
   * Adds description of hydrogen bonds of an atom.
   * 
   * @param atom
   *          The atom to describe.
   */
  private void describeHydrogenBonds(RichAtom atom) {
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


  private void describeSuperSystem(final RichAtom atom) {
    this.shortDescription = true;
    for (String context : atom.getContexts()) {
      if (RichStructureHelper.isAtomSet(context)) {
        RichAtomSet set = RichStructureHelper.getRichAtomSet(context);
        RichSetType type = set.getType();
        if (type == RichSetType.FUNCGROUP
            || type == RichSetType.ISOLATED
            || type == RichSetType.FUSED
            || type == RichSetType.ALIPHATIC) {
          set.accept(this);
        }
      }
    }
    this.shortDescription = false;
  }

}
