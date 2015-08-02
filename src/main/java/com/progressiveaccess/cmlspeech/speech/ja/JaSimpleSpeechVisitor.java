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
 * @file   JaSimpleSpeechVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jul  8 09:07:00 2015
 *
 *a @brief  Visitor for simple speech descriptions.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.ja;

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

public class JaSimpleSpeechVisitor extends JaSpeechVisitor {

  @Override
  public void visit(final RichIsolatedRing ring) {
    this.addSpeech(ring.getComponentsPositions().size());
    this.addSpeech("員"); // Elements
    this.addSpeech("環"); // Ring
    this.addSpeech("、"); // Punctuation
    if (this.getFlag("short")) {
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
    if (this.getFlag("short")) {
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
    if (this.getFlag("short")) {
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
    if (this.getFlag("short")) {
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
    this.setFlag("short", true);
    Integer count = 0;
    for (String set : molecule.getPath()) {
      ((RichChemObject)
       RichStructureHelper.getRichStructure(set)).accept(this);
      count++;
      if (count == 1) {
        this.remSpeech();
        this.addSpeech("と、"); // and Punctuation
      }
    }
    this.remSpeech();
    this.addSpeech("で構成された分子");  // Molecule consisting of
    this.addSpeech("、"); // Punctuation
    this.setFlag("short", false);
  }


  @Override
  protected final void describeReplacements(final RichAtomSet system) {
    final Iterator<String> iterator = system.iterator();
    while (iterator.hasNext()) {
      final String value = iterator.next();
      final RichAtom atom = RichStructureHelper.getRichAtom(value);
      if (!atom.isCarbon()) {
        this.addSpeech(system.getPosition(value));
        this.addSpeech("位"); // Position symbol
        this.addSpeech("は"); // at
        this.addSpeech(Language.getAtomTable().lookup(atom));
        this.addSpeech("、"); // Punctuation
      }
    }
  }


  @Override
  protected final void describeMultiBonds(final RichAtomSet system) {
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

}
