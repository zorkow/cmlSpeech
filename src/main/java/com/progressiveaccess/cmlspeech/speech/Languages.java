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
 * @file   Languages.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug 16 18:00:20 2015
 *
 * @brief Class that takes care of the generation of messages in all languages.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.base.FileHandler;
import com.progressiveaccess.cmlspeech.base.Logger;
import com.progressiveaccess.cmlspeech.sre.SreElement;
import com.progressiveaccess.cmlspeech.sre.SreMessages;
import com.progressiveaccess.cmlspeech.sre.XmlVisitable;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Elements;

import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.Callable;


/**
 * Generates messages in all given languages and combines them in a final
 * message element.
 */
public final class Languages {

  private static final String MSG_PREFIX = "SRE_MSG_";

  private static final PriorityQueue<String> LANGUAGES = new PriorityQueue<>();
  private static final Map<String, SreMessages> MESSAGES = new TreeMap<>();
  private static Integer msgCounter = 0;


  /** Dummy constructor. */
  private Languages() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Clears internal state.
   */
  public static void clear() {
    Languages.MESSAGES.clear();
    Languages.LANGUAGES.clear();
    Languages.msgCounter = 0;
  }


  /**
   * Sets the languages for which speech output will be creates.
   *
   * @param langs
   *          A string with a comma separated list of languages.
   */
  public static void set(final String langs) {
    Languages.clear();
    if (langs == null) {
      Languages.LANGUAGES.addAll(IsoTable.existing());
    } else {
      for (String language : langs.split(",")) {
        String iso = IsoTable.lookup(language);
        if (IsoTable.implemented(iso)) {
          Languages.LANGUAGES.add(iso);
        }
      }
    }
    if (Languages.LANGUAGES.size() == 0) {
      Languages.LANGUAGES.add("en");
    }
    for (String language : Languages.LANGUAGES) {
      Languages.MESSAGES.put(language, new SreMessages(language));
    }
  }


  /**
   * Generates expert speech output.
   *
   * @param visitable
   *          The element for which speech is produced.
   * @param positions
   *          The position of the context in which element lives.
   *
   * @return The abstract message string for the generated speech.
   */
  public static String expertSpeech(final XmlVisitable visitable,
                                    final ComponentsPositions positions) {
    return Languages.speech(visitable, positions,
                            () -> { return Language.getExpertSpeechVisitor(); }
                            );
  }


  /**
   * Generates simple speech output.
   *
   * @param visitable
   *          The element for which speech is produced.
   * @param positions
   *          The position of the context in which element lives.
   *
   * @return The abstract message string for the generated speech.
   */
  public static String simpleSpeech(final XmlVisitable visitable,
                                    final ComponentsPositions positions) {
    return Languages.speech(visitable, positions,
                            () -> { return Language.getSimpleSpeechVisitor(); }
                            );
  }


  /**
   * Generates speech output.
   *
   * @param visitable
   *          The element for which speech is produced.
   * @param positions
   *          The position of the context in which element lives.
   * @param caller
   *          A lambda expression that produces a speech visitor.
   *
   * @return The abstract message string for the generated speech.
   */
  private static String speech(final XmlVisitable visitable,
                               final ComponentsPositions positions,
                               final Callable<SpeechVisitor> caller) {
    String msg = MSG_PREFIX + msgCounter++;
    for (String language : Languages.MESSAGES.keySet()) {
      SpeechVisitor visitor;
      Language.reset(language);
      try {
        visitor = caller.call();
      } catch (Exception e) {
        Logger.error("Unknown visitor for language " + language);
        continue;
      }
      visitor.setContextPositions(positions);
      visitable.accept(visitor);
      Languages.MESSAGES.get(language).put(msg, visitor.getSpeech());
    }
    return msg;
  }


  /**
   * Appends the message nodes to the given element.
   *
   * @param element
   *          Messages are appended as children to this element.
   */
  // TODO (sorge) Consolidate the same message values?
  public static void append(final SreElement element) {
    for (String key : Languages.MESSAGES.keySet()) {
      element.appendChild(Languages.MESSAGES.get(key).toXml());
    }
  }


  /**
   * Replaces message statement in the element with the actual speech strings.
   *
   * @param element
   *          The annotation element of the molecule.
   */
  public static void replace(final SreElement element) {
    Iterator<SreMessages> iter = Languages.MESSAGES.values().iterator();
    if (iter.hasNext()) {
      SreMessages language = iter.next();
      Languages.replace(element, language);
    }
  }


  /**
   * Replaces message statement in the element with the actual speech strings.
   *
   * @param element
   *          The annotation element of the molecule.
   * @param language
   *          The language messages.
   */
  private static void replace(final SreElement element,
                              final SreMessages language) {
    for (Integer i = 0; i < element.getAttributeCount(); i++) {
      Attribute attribute = element.getAttribute(i);
      String value = language.get(attribute.getValue());
      if (value != null) {
        attribute.setValue(value);
      }
    }
    Elements elements = element.getChildElements();
    for (Integer i = 0; i < elements.size(); i++) {
      Languages.replace((SreElement) elements.get(i), language);
    }
  }


  /**
   * Writes localised messages to separate output files with iso language
   * extension.
   *
   * @param fileName
   *          The base filename.
   */
  public static void toFile(final String fileName) {
    for (String key : Languages.MESSAGES.keySet()) {
      Document xml = new Document(Languages.MESSAGES.get(key).toXml());
      FileHandler.writeXom(xml, fileName, key);
    }
  }

}
