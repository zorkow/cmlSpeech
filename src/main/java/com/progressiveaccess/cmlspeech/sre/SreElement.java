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
 * @file   SreElement.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:31:09 2015
 *
 * @brief  Sre Elements
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import nu.xom.Element;

/**
 * Basic elements for Sre annotations.
 */

public class SreElement extends Element {

  SreElement(final String tag) {
    super(SreNamespace.getInstance().prefix + ":" + tag, SreNamespace
        .getInstance().uri);
  }

  public SreElement(final SreNamespace.Tag tag) {
    super(tag.tag, SreNamespace.getInstance().uri);
  }

  public SreElement(final SreNamespace.Tag tag, final String text) {
    super(tag.tag, SreNamespace.getInstance().uri);
    this.appendChild(text);
  }

  public SreElement(final SreNamespace.Tag tag, final Element child1,
      final Element child2) {
    super(tag.tag, SreNamespace.getInstance().uri);
    this.appendChild(child1);
    this.appendChild(child2);
  }

  public void appendChild(final SreElement element) {
    if (element != null) {
      super.appendChild(element);
    }
  }

}