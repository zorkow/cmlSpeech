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
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug 16 18:00:20 2015
 * 
 * @brief Class that takes care of the generation of messages in all languages.
 * 
 * 
 */

//
package com.progressiveaccess.cmlspeech.speech;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import com.progressiveaccess.cmlspeech.sre.SreMessages;
import com.progressiveaccess.cmlspeech.sre.XmlVisitable;
import com.progressiveaccess.cmlspeech.sre.SreElement;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;


/**
 * Generates messages in all given languages and combines them in a final
 * message element.
 */
public final class Languages {

  private static String MSG_PREFIX = "SRE_MSG_";
    
  private static PriorityQueue<String> languages = new PriorityQueue<>();
  // TODO: (sorge) This could be written as an object pool with reusing existing
  // hashmaps.
  private static Map<String, SreMessages> messages = new TreeMap<>();
  private static Integer msgCounter = 0;
  
  
  /** Dummy constructor. */
  private Languages() {
    throw new AssertionError("Instantiating utility class...");
  }


  public static void set(String languages) {
    if (languages == null) {
      // Here get all existing languages!
      Languages.languages = IsoTable.existing();
    } else {
      for (String language : languages.split(",")) {
        String iso = IsoTable.lookup(language);
        if (IsoTable.implemented(iso)) {
          Languages.languages.add(iso);
        }
      }
    }
    if (Languages.languages.size() == 0) {
      Languages.languages.add("en");
    }
    for (String language : Languages.languages) {
      Languages.messages.put(language, new SreMessages(language));
    }
    msgCounter = 0;
  }

  
  public static String expertSpeech(XmlVisitable visitable,
                                    ComponentsPositions positions) {
    String msg = MSG_PREFIX + msgCounter++;
    for (String language : Languages.messages.keySet()) {
      Language.reset(language);
      SpeechVisitor visitor = Language.getExpertSpeechVisitor();
      visitor.setContextPositions(positions);
      visitable.accept(visitor);
      Languages.messages.get(language).put(msg, visitor.getSpeech());
    }
    return msg;
  }

  
  public static String simpleSpeech(XmlVisitable visitable,
                                    ComponentsPositions positions) {
    String msg = MSG_PREFIX + msgCounter++;
    for (String language : Languages.messages.keySet()) {
      Language.reset(language);
      SpeechVisitor visitor = Language.getSimpleSpeechVisitor();
      visitor.setContextPositions(positions);
      visitable.accept(visitor);
      Languages.messages.get(language).put(msg, visitor.getSpeech());
    }
    return msg;
  }

  
  // TODO: (sorge) Consolidate the same message values?
  public static void append(SreElement element) {
    for (String key : Languages.messages.keySet()) {
      element.appendChild(Languages.messages.get(key).toXml());
    }
  }
  
}
