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
 * @file   JaBondTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug  2 12:58:22 2015
 *
 * @brief  Singleton class to translate bond names in Japanese.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.ja;

import com.progressiveaccess.cmlspeech.speech.AbstractBondTable;


/**
 * Maps bond identifiers to their proper names.
 */

public final class JaBondTable extends AbstractBondTable {

  private static final long serialVersionUID = 1L;

  public JaBondTable() {
    this.put("single", "単");
    this.put("double", "二重");
    this.put("triple", "三重");
    this.put("quadruple", "四重");
    this.put("quintuple", "五重");
    this.put("sextuple", "六重");
  }

}
