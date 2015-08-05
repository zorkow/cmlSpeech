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
 * @file   SpiderName.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jul 21 20:12:00 2015
 *
 * @brief  Class to hold different types of names for a structure.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

import java.util.ArrayList;
import com.progressiveaccess.cmlspeech.speech.Language;
import java.util.stream.Stream;

/**
 * Collection of SpiderNames.
 */
public class SpiderNames extends ArrayList<SpiderName> {

  private static final long serialVersionUID = 1L;

  public final String computeName() {
    String language = Language.getLanguage();
    String name = this.computeName(language);
    if (!name.equals("") || language.equals("en")) {
      return name;
    }
    return this.computeName("en");
  }


  private final String computeName(String language) {
    String name = this.computeName(language, "name");
    if (!name.equals("")) {
      return name;
    }
    return this.computeName(language, "iupac");
  }


  private final String computeName(String language, String type) {
    SpiderName name = this.find(language, type);
    if (name == null) {
      return "";
    }
    return name.getName();
  }


  private SpiderName find(String language, String type) {
    for (SpiderName name : this) {
      if (name.getLanguage().equals(language)
          && name.getType().equals(type)) {
        return name;
      }
    }
    return null;
  }

}
