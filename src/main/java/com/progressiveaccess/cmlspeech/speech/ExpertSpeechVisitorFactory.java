// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file   ExpertSpeechVisitorFactory.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Thu Jul 30 05:33:44 2015
 * 
 * @brief  Factory for generating atom visitors.
 * 
 * 
 */

//
package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.speech.en.EnglishExpertSpeechVisitor;
import com.progressiveaccess.cmlspeech.speech.ja.JapaneseExpertSpeechVisitor;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory for generating language specific atom visitors.
 */

public class ExpertSpeechVisitorFactory {

  private static Map<String, SpeechVisitor> VISITORS;

  static {
    VISITORS = new HashMap<String, SpeechVisitor>();
    VISITORS.put("english", new EnglishExpertSpeechVisitor());
  }
  
  public static SpeechVisitor getSpeechVisitor(String language) {
    SpeechVisitor visitor = VISITORS.get(language);
    if (visitor != null) {
      return visitor;
    }
    switch (language) {
    case "japanese":
      visitor = new JapaneseExpertSpeechVisitor(); 
      break;
    default:
      return VISITORS.get("english");
    }
    VISITORS.put(language, visitor);
    return visitor;
  }
  
}
