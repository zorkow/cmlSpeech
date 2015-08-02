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
 * @file AbstractSpeechVisitor.java
 * @author Volker Sorge <a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date Sat Aug 1 19:14:46 2015
 *
 * @brief Abstract class for speech visitors.
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Basic functionality shared by all speech visitors.
 */

public abstract class AbstractSpeechVisitor implements SpeechVisitor {

  private ComponentsPositions contextPositions = null;
  private final LinkedList<String> speech = new LinkedList<String>();
  private final Map<String, Boolean> flags = new HashMap<String, Boolean>();


  public AbstractSpeechVisitor() {
    this.init();
  }


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
    final String last = this.speech.removeLast();
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


  @Override
  public String getSpeech() {
    final Joiner joiner = Joiner.on(" ");
    final String result = joiner.join(this.retrieveSpeech());
    this.clearSpeech();
    return result + ".";
  }


  /**
   * Sets a flag to either true or false.
   *
   * @param flag
   *          The flag to be set.
   * @param value
   *          The binary value fo the flag.
   */
  protected final void setFlag(final String flag, final boolean value) {
    this.flags.put(flag, value);
  }


  /**
   * Returns the value of a given flag. If the flag does not exist it simply
   * returns false.
   *
   * @param flag
   *          The name of the flag.
   *
   * @return The boolean value of the flag.
   */
  protected final boolean getFlag(final String flag) {
    final Boolean value = this.flags.get(flag);
    return value == null ? false : value;
  }


  /**
   * Any initialisations that need to be done before the visitor is called. For
   * example, some flags might need to be set.
   */
  protected void init() {
  }


  /**
   * Adds description of the supersystem an atom belongs to.
   *
   * @param atom
   *          The original atom.
   */
  protected void describeSuperSystem(final RichAtom atom) {
    this.setFlag("short", true);
    for (final String context : atom.getContexts()) {
      if (RichStructureHelper.isAtomSet(context)) {
        final RichAtomSet set = RichStructureHelper.getRichAtomSet(context);
        final RichSetType type = set.getType();
        if (type == RichSetType.FUNCGROUP
            || type == RichSetType.ISOLATED
            || type == RichSetType.FUSED
            || type == RichSetType.ALIPHATIC) {
          set.accept(this);
        }
      }
    }
    this.setFlag("short", false);
  }


  /**
   * Adds description of hydrogen bonds of an atom.
   *
   * @param atom
   *          The atom to describe.
   */
  protected abstract void describeHydrogenBonds(final RichAtom atom);


  /**
   * Adds description of external substitutions for atom sets.
   *
   * @param system
   *          The atom set to describe.
   */
  protected abstract void describeSubstitutions(final RichAtomSet system);


  /**
   * Adds description of internal replacements for an atom set.
   *
   * @param system
   *          The ring system to describe.
   */
  protected abstract void describeReplacements(final RichAtomSet system);


  /**
   * Describes multi bonds in rings and chains.
   *
   * @param system
   *          The atom set to describe.
   */
  // TODO (sorge) Sort those bonds. Maybe combine with a more stateful walk.
  protected abstract void describeMultiBonds(final RichAtomSet system);


  /**
   * Adds the name of an atom set.
   *
   * @param atomset
   *          The atom set.
   */
  protected void addName(final RichAtomSet atomset) {
    // TODO (sorge) Replace this function with language specific naming.
    // Do something about all upper case names without destroying
    // important upper cases. E.g.: WordUtils.capitalizeFully.
    if (!atomset.getName().equals("")) {
      this.addSpeech(atomset.getName());
      return;
    }
    if (!atomset.getIupac().equals("")) {
      this.addSpeech(atomset.getIupac());
      return;
    }
    this.addSpeech(atomset.getMolecularFormula());
  }

}
