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

import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;

/**
 * Produces the basic speech for structures.
 */

public class EnExpertSpeechVisitor extends EnSpeechVisitor {

  @Override
  public void visit(final RichIsolatedRing ring) {
    this.addName(ring);
    this.addSpeech("ring");
    if (this.getFlag("short")) {
      return;
    }
    this.describeSubstitutions(ring);
  }


  @Override
  public void visit(final RichFusedRing ring) {
    this.addSpeech("Fused ring system");
    this.addName(ring);
    if (this.getFlag("short")) {
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
    if (this.getFlag("short")) {
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
  protected final void describeReplacements(final RichAtomSet system) { }

  
  @Override
  protected final void describeMultiBonds(final RichAtomSet system) { }

}
