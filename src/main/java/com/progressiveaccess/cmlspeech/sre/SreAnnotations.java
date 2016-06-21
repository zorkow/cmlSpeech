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
 * @file   SreAnnotations.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:21:38 2015
 *
 * @brief  XML annotations structures.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.base.CmlNameComparator;

import com.google.common.collect.TreeMultimap;

import java.util.Comparator;

/**
 * Basic class to add annotations like speech and structural representations to
 * CML objects.
 */

public class SreAnnotations extends SreElement {

  private final TreeMultimap<String, SreElement> annotations;


  /**
   * Constructor of annotations. Elements are held in a multimap until they are
   * combined.
   */
  SreAnnotations() {
    super(SreNamespace.Tag.ANNOTATIONS);
    this.annotations =
      TreeMultimap.create(new CmlNameComparator(), new SreComparator());
  }


  /**
   * Dummy comparator for the tree multi map.
   */
  private class SreComparator implements Comparator<SreElement> {

    @Override
    public int compare(final SreElement element1, final SreElement element2) {
      return 1;
    }
  }


  /**
   * Register a new annotation element by its id.
   *
   * @param id
   *          The id of the new element.
   * @param element
   *          The actual element.
   */
  public void registerAnnotation(final String id, final SreElement element) {
    this.annotations.put(id, element);
  }


  /**
   * Completes the annotation element by combining the single elements held in
   * the multimap.
   */
  public void complete() {
    for (final String key : this.annotations.keySet()) {
      for (final SreElement value : this.annotations.get(key)) {
        this.appendChild(value);
      }
    }
  }


  @Override
  public String toString() {
    String result = "";
    for (final String key : this.annotations.keySet()) {
      for (final SreElement value : this.annotations.get(key)) {
        result += key + ": " + value.toXML() + "\n";
      }
    }
    return result;
  }

}
