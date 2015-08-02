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
 * @file   EnExpertSpeechVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jun 30 14:46:54 2015
 *
 * @brief  Simple speech visitor.
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
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSetType;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Produces the basic speech for structures.
 */

public class EnExpertSpeechVisitor extends AbstractSpeechVisitor {

  private boolean shortDescription = false;


  @Override
  public void visit(final RichBond bond) {
    super.visit(bond);
    this.addSpeech("bond");
  }


  @Override
  public void visit(final RichAtom atom) {
    Integer position = this.getContextPositions().getPosition(atom.getId());
    // TODO (sorge) Maybe take the supersystem of the atom outside the context.
    if (position == null) {
      this.describeSuperSystem(atom);
      return;
    }
    super.visit(atom);
    this.addSpeech(position);
    if (this.shortDescription) {
      return;
    }
    this.describeHydrogenBonds(atom);
  }


  @Override
  public void visit(final RichIsolatedRing ring) {
    this.addName(ring);
    this.addSpeech("ring");
    if (this.shortDescription) {
      return;
    }
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichFusedRing ring) {
    this.addSpeech("Fused ring system");
    this.addName(ring);
    if (this.shortDescription) {
      return;
    }
    this.addSpeech("with");
    this.addSpeech(ring.getSubSystems().size());
    this.addSpeech("subrings");
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichSubRing ring) {
    this.addSpeech("Subring");
    this.addName(ring);
  }


  @Override
  public void visit(final RichAliphaticChain chain) {
    this.addName(chain);
    if (this.shortDescription) {
      return;
    }
    this.describeSubstitutions(chain);
  }


  @Override
  public void visit(final RichFunctionalGroup group) {
    this.addSpeech("Functional group");
    this.addName(group);
  }


  @Override
  public void visit(final RichMolecule molecule) {
    this.addName(molecule);
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
    this.shortDescription = true;
    RichStructureHelper.getRichBond(bond.getConnector()).accept(this);
    // TODO (sorge) The past tense here is problematic!
    this.modSpeech("ed");
    this.addSpeech("to");
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


  // TODO (sorge) Do something about all upper case names without destroying
  // important upper cases. E.g.: WordUtils.capitalizeFully.
  private void addName(final RichAtomSet atomset) {
    if (!atomset.getName().equals("")) {
      addSpeech(atomset.getName());
      return;
    }
    if (!atomset.getIupac().equals("")) {
      addSpeech(atomset.getIupac());
      return;
    }
    addSpeech(atomset.getMolecularFormula());
  }


  // TODO (sorge) For the following utility functions, see if they can be
  // refactored with walk methods, etc.
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
        this.remSpeech();
    }
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
