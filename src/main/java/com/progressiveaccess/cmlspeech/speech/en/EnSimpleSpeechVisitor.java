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
 * @file   EnSimpleSpeechVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jul  8 09:07:00 2015
 *
 *a @brief  Visitor for simple speech descriptions.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.en;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.speech.Language;
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

import java.util.Iterator;

/**
 * Produces the simple speech for structures.
 */

public class EnSimpleSpeechVisitor extends EnSpeechVisitor {

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
    this.remSpeech();
    this.shortDescription = false;
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
        this.addSpeech(Language.getAtomTable().lookup(atom));
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

}
