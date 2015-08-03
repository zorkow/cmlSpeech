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
 * @file   SreAttribute.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:29:44 2015
 *
 * @brief  Attributes for speech annotations.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import nu.xom.Attribute;


/**
 * Attribute structure.
 */
public class SreAttribute extends Attribute {

  /**
   * Constructs an SRE attribute.
   *
   * @param localName
   *          The local name of the attribute.
   * @param value
   *          The attribute value.
   */
  public SreAttribute(final String localName, final String value) {
    super(SreNamespace.getInstance().getPrefix() + ":" + localName, SreNamespace
        .getInstance().getUri(), value);
  }


  /**
   * Constructs a new SRE attribute from a given one.
   *
   * @param attr
   *          An SRE attribute.
   * @param value
   *          The new attribute value.
   */
  public SreAttribute(final SreNamespace.Attribute attr, final String value) {
    super(SreNamespace.getInstance().getPrefix() + ":" + attr.getAttribute(),
        SreNamespace.getInstance().getUri(), value);
  }

}
