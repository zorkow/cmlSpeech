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
 * @file   SimpleSpeechVisitor.java
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
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Produces the simple speech for structures.
 */

public class SimpleSpeechVisitor implements XmlVisitor {

  private ComponentsPositions contextPositions = null;
  private LinkedList<String> speech = new LinkedList<String>();
  private boolean shortDescription = false;


  public void setContextPositions(final ComponentsPositions positions) {
    this.contextPositions = positions;
  }


  @Override
  public void visit(final RichBond bond) {
    this.addSpeech(bond.getName());
  }


  @Override
  public void visit(final RichAtom atom) {
    Integer position = this.contextPositions.getPosition(atom.getId());
    // TODO (sorge) Maybe take the supersystem of the atom outside the context.
    if (position == null) {
      this.describeSuperSystem(atom);
      return;
    }
    this.addSpeech(atom.getName());
    this.addSpeech(position);
    if (this.shortDescription) {
      return;
    }
    this.describeHydrogenBonds(atom);
  }


  @Override
  public void visit(final RichIsolatedRing ring) {
    this.addSpeech("Ring");
    this.addSpeech("with");
    this.addSpeech(ring.getComponentsPositions().size());
    this.addSpeech("elements");
    if (this.shortDescription) {
      return;
    }
    this.describeReplacements(ring);
    this.describeMultiBonds(ring);
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichFusedRing ring) {
    this.addSpeech("Fused ring system");
    this.addSpeech("with");
    this.addSpeech(ring.getSubSystems().size());
    this.addSpeech("subrings");
    if (this.shortDescription) {
      return;
    }
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichSubRing ring) {
    this.addSpeech("Subring");
    this.addSpeech("with");
    this.addSpeech(ring.getComponentsPositions().size());
    this.addSpeech("elements");
    if (this.shortDescription) {
      return;
    }
    this.describeReplacements(ring);
    this.describeMultiBonds(ring);
  }


  @Override
  public void visit(final RichAliphaticChain chain) {
    this.addSpeech("Aliphatic chain");
    this.addSpeech("of length");
    this.addSpeech(chain.getComponentsPositions().size());
    if (this.shortDescription) {
      return;
    }
    this.describeReplacements(chain);
    this.describeMultiBonds(chain);
    this.describeSubstitutions(chain);
  }


  @Override
  public void visit(final RichFunctionalGroup group) {
    this.addSpeech("Functional group");
    this.addSpeech(group.getStructuralFormula());
  }


  @Override
  public void visit(final RichMolecule molecule) {
    this.addSpeech("Molecule");
    this.addSpeech("consisting of");
    this.shortDescription = true;
    for (String set : molecule.getPath()) {
      ((RichChemObject)
       RichStructureHelper.getRichStructure(set)).accept(this);
      this.addSpeech("and");
    }
    this.speech.removeLast();
    this.shortDescription = false;
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
    this.shortDescription = true;
    this.addSpeech("spiro atom");
    RichStructureHelper.getRichAtom(spiroAtom.getConnector()).accept(this);
    this.addSpeech("to");
    RichStructureHelper.getRichAtomSet(spiroAtom.getConnected()).accept(this);
    this.shortDescription = false;
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
    this.addSpeech("bridge atom");
    RichStructureHelper.getRichAtom(bridgeAtom.getConnector()).accept(this);
  }


  @Override
  public void visit(final ConnectingBond bond) {
    RichStructureHelper.getRichBond(bond.getConnector()).accept(this);
    // TODO (sorge) The past tense here is problematic!
    this.modSpeech("ed");
    this.addSpeech("to");
    this.shortDescription = true;
    String connected = bond.getConnected();
    if (RichStructureHelper.isAtom(connected)) {
      RichStructureHelper.getRichAtom(connected).accept(this);
    } else {
      RichStructureHelper.getRichAtomSet(connected).accept(this);
    }
    this.shortDescription = false;
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.shortDescription = true;
    this.addSpeech("shared atom");
    RichStructureHelper.getRichAtom(sharedAtom.getConnector()).accept(this);
    this.addSpeech("with");
    RichStructureHelper.getRichAtomSet(sharedAtom.getConnected()).accept(this);
    this.shortDescription = false;
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    this.addSpeech("shared");
    RichStructureHelper.getRichBond(sharedBond.getConnector()).accept(this);
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
    final Joiner joiner = Joiner.on(" ");
    String result = joiner.join(this.speech);
    this.speech.clear();
    return result + ".";
  }


  // TODO (sorge) For the following utility functions, see if they can be
  // refactored with walk methods, etc.
  private void describeReplacements(final RichAtomSet system) {
    final Iterator<String> iterator = system.iterator();
    while (iterator.hasNext()) {
      final String value = iterator.next();
      final RichAtom atom = RichStructureHelper.getRichAtom(value);
      if (!atom.isCarbon()) {
        this.addSpeech("with");
        this.addSpeech(atom.getName());
        this.addSpeech("at position");
        this.addSpeech(system.getPosition(value));
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
      bond.accept(this);
      this.addSpeech("between positions");
      this.addSpeech(atomA);
      this.addSpeech("and");
      this.addSpeech(atomB);
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
        this.addSpeech("Substitution at position");
        this.addSpeech(subst.iterator().next());
        return;
      default:
        this.addSpeech("Substitutions at positions");
        for (final Integer position : subst) {
          this.addSpeech(position);
          this.addSpeech("and");
        }
        this.speech.removeLast();
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
        this.addSpeech("bonded to");
        this.addSpeech(count.toString());
        this.addSpeech("hydrogen");
        return;
      default:
        this.addSpeech("bonded to");
        this.addSpeech(count.toString());
        this.addSpeech("hydrogens");
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
