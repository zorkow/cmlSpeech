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
 * @file   Language.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Jul 30 05:04:37 2015
 * 
 * @brief  Abstract Factory that holds localisable visitors, etc.
 * 
 * 
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.base.Cli;

/**
 * Holds the visitors producing speech output.
 */

public final class Language {

  private static String language = Cli.hasOption("int") ?
      Cli.getOptionValue("int") : "english";
  private static AtomTable atomTable = AtomTableFactory.getAtomTable(language);
  private static SpeechVisitor simpleSpeechVisitor =
      SimpleSpeechVisitorFactory.getSpeechVisitor(language);
  private static SpeechVisitor expertSpeechVisitor =
      ExpertSpeechVisitorFactory.getSpeechVisitor(language);

  public static AtomTable getAtomTable() {
    return Language.atomTable;
  }
  

  public static SpeechVisitor getSimpleSpeechVisitor() {
    return Language.simpleSpeechVisitor;
  }
  

  public static SpeechVisitor getExpertSpeechVisitor() {
    return Language.expertSpeechVisitor;
  }

}
