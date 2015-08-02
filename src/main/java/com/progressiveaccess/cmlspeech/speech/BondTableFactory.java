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
 * @file   BondTableFactory.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Sun Aug  2 12:58:22 2015
 * 
 * @brief  Factory for generating bond tables.
 * 
 * 
 */

//
package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.speech.en.EnBondTable;
import com.progressiveaccess.cmlspeech.speech.ja.JaBondTable;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory for generating language specific bond tables.
 */

public class BondTableFactory {

  private static Map<String, BondTable> BOND_TABLES;

  static {
    BOND_TABLES = new HashMap<String, BondTable>();
    BOND_TABLES.put("english", new EnBondTable());
  }
  
  public static BondTable getBondTable(String language) {
    BondTable table = BOND_TABLES.get(language);
    if (table != null) {
      return table;
    }
    switch (language) {
    case "japanese":
      table = new JaBondTable(); 
      break;
    default:
      return BOND_TABLES.get("english");
    }
    BOND_TABLES.put(language, table);
    return table;
  }
  
}
