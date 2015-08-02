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
 * @file AtomTableFactory.java
 * @author Volker Sorge <a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date Thu Jul 30 05:33:44 2015
 *
 * @brief Factory for generating atom tables.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.speech.en.EnAtomTable;
import com.progressiveaccess.cmlspeech.speech.ja.JaAtomTable;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory for generating language specific atom tables.
 */

public final class AtomTableFactory {

  private static Map<String, AtomTable> atomTables;

  static {
    atomTables = new HashMap<String, AtomTable>();
    atomTables.put("en", new EnAtomTable());
  }


  /** Dummy constructor. */
  private AtomTableFactory() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Constructs the atom table for the current language.
   *
   * @param language
   *          A language string.
   *
   * @return The atom table for the given language.
   */
  public static AtomTable getAtomTable(final String language) {
    AtomTable table = atomTables.get(language);
    if (table != null) {
      return table;
    }
    switch (language) {
      case "ja":
        table = new JaAtomTable();
        break;
      default:
        return atomTables.get("en");
    }
    atomTables.put(language, table);
    return table;
  }

}
