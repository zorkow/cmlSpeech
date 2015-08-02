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
 * @file   AbstractSpeechVisitor.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Aug  1 19:14:46 2015
 *
 * @brief  Abstract class for speech visitors.
 *
 *
 */

//
package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichSetType;

import com.google.common.base.Joiner;

import java.util.LinkedList;

/**
 * Basic functionality shared by all speech visitors.
 */

public abstract class AbstractSpeechVisitor implements SpeechVisitor {

  private ComponentsPositions contextPositions = null;
  private LinkedList<String> speech = new LinkedList<String>();
  // TODO
  protected boolean shortDescription = false;


  @Override
  public void setContextPositions(final ComponentsPositions positions) {
    this.contextPositions = positions;
  }


  @Override
  public ComponentsPositions getContextPositions() {
    return this.contextPositions;
  }


  protected void remSpeech() {
    this.speech.removeLast();
  }


  protected void modSpeech(final String msg) {
    String last = this.speech.removeLast();
    this.speech.offerLast(last + msg);
  }


  protected void addSpeech(final String msg) {
    if (!msg.equals("")) {
      this.speech.add(msg);
    }
  }


  protected void addSpeech(final Integer num) {
    this.addSpeech(num.toString());
  }


  protected LinkedList<String> retrieveSpeech() {
    return this.speech;
  }


  protected void clearSpeech() {
    this.speech.clear();
  }


  protected void describeSuperSystem(final RichAtom atom) {
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


  public String getSpeech() {
    final Joiner joiner = Joiner.on(" ");
    String result = joiner.join(this.retrieveSpeech());
    this.clearSpeech();
    return result + ".";
  }

}
