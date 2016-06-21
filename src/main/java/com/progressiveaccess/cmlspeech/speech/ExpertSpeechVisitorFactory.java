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
 * @file ExpertSpeechVisitorFactory.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date Thu Jul 30 05:33:44 2015
 *
 * @brief Factory for generating expert speech visitors.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;


/**
 * Factory for generating language specific expert speech visitors.
 */

public final class ExpertSpeechVisitorFactory {

  private static final LanguageFactory<SpeechVisitor> TABLE =
      new LanguageFactory<SpeechVisitor>("ExpertSpeechVisitor");

  /** Dummy constructor. */
  private ExpertSpeechVisitorFactory() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Constructs the expert speech visitor for the current language.
   *
   * @param language
   *          A language string.
   *
   * @return The expert speech visitor for the given language.
   */
  public static SpeechVisitor getSpeechVisitor(final String language) {
    return TABLE.getLanguageVisitor(language);
  }

}
