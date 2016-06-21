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

  /**
   * Constructor for SRE element.
   *
   * @param tag
   *          The tag name of the element.
   */
  SreElement(final String tag) {
    super(SreNamespace.getInstance().getPrefix() + ":" + tag, SreNamespace
        .getInstance().getUri());
  }


  /**
   * Constructor for SRE element.
   *
   * @param tag
   *          The tag of the element.
   */
  public SreElement(final SreNamespace.Tag tag) {
    super(tag.getTag(), SreNamespace.getInstance().getUri());
  }


  /**
   * Constructor for SRE element with text content.
   *
   * @param tag
   *          The tag name of the element.
   * @param text
   *          The text content to append.
   */
  public SreElement(final SreNamespace.Tag tag, final String text) {
    super(tag.getTag(), SreNamespace.getInstance().getUri());
    this.appendChild(text);
  }


  /**
   * Constructor for SRE element with two children.
   *
   * @param tag
   *          The tag name of the element.
   * @param child1
   *          The first child.
   * @param child2
   *          The second child.
   */
  public SreElement(final SreNamespace.Tag tag, final Element child1,
      final Element child2) {
    super(tag.getTag(), SreNamespace.getInstance().getUri());
    this.appendChild(child1);
    this.appendChild(child2);
  }


  /**
   * Appends a child element to this SRE element.
   *
   * @param element
   *          The element to append.
   */
  public void appendChild(final SreElement element) {
    if (element != null) {
      super.appendChild(element);
    }
  }

}
