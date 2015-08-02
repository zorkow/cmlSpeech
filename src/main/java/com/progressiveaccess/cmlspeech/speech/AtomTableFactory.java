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
 * @file   AtomTableFactory.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Thu Jul 30 05:33:44 2015
 * 
 * @brief  Factory for generating atom tables.
 * 
 * 
 */

//
package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.speech.en.EnglishAtomTable;
import com.progressiveaccess.cmlspeech.speech.ja.JapaneseAtomTable;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory for generating language specific atom tables.
 */

public class AtomTableFactory {

  private static Map<String, AtomTable> ATOM_TABLES;

  static {
    ATOM_TABLES = new HashMap<String, AtomTable>();
    ATOM_TABLES.put("english", new EnglishAtomTable());
  }
  
  public static AtomTable getAtomTable(String language) {
    AtomTable table = ATOM_TABLES.get(language);
    if (table != null) {
      return table;
    }
    switch (language) {
    case "japanese":
      table = new JapaneseAtomTable(); 
      break;
    default:
      return ATOM_TABLES.get("english");
    }
    ATOM_TABLES.put(language, table);
    return table;
  }
  
}
