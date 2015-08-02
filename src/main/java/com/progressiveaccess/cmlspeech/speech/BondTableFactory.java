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
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
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

public final class BondTableFactory {

  private static Map<String, BondTable> bondTables;

  static {
    bondTables = new HashMap<String, BondTable>();
    bondTables.put("english", new EnBondTable());
  }


  /** Dummy constructor. */
  private BondTableFactory() {
    throw new AssertionError("Instantiating utility class...");
  }


  public static BondTable getBondTable(final String language) {
    BondTable table = bondTables.get(language);
    if (table != null) {
      return table;
    }
    switch (language) {
      case "japanese":
        table = new JaBondTable();
        break;
      default:
        return bondTables.get("english");
    }
    bondTables.put(language, table);
    return table;
  }

}
