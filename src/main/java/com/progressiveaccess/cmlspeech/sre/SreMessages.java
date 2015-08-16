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
 * @file   SreMessages.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug 16 17:37:20 2015
 * 
 * @brief  Storage for language specific message components.
 * 
 * 
 */

//
package com.progressiveaccess.cmlspeech.sre;

import java.util.TreeMap;

/**
 * Hashmap for language specific messages.
 */
public class SreMessages extends TreeMap<String, String> {

  private static final long serialVersionUID = 1L;

  private String language;

  public SreMessages(String language) {
    this.language = language;
  }


  public final SreElement toXml() {
    SreElement xml = new SreElement(SreNamespace.Tag.MESSAGES);
    xml.appendChild(new SreElement(SreNamespace.Tag.LANGUAGE, this.language));
    for (String key : this.keySet()) {
      SreElement msg = new SreElement(SreNamespace.Tag.MESSAGE, this.get(key));
      msg.addAttribute(new SreAttribute(SreNamespace.Attribute.MSG, key));
      xml.appendChild(msg);
    }
    return xml;
  }
  
}
