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
 * @file   SreXML.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Jun 19 16:34:40 2014
 *
 * @brief  Abstract class to handle SRE annotations.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;

import java.util.Set;

/**
 * Abstract class for XML like Sre annotations.
 */

public abstract class SreXml {

  SreAnnotations annotations;

  SreXml() {
    this.annotations = new SreAnnotations();
  }

  public SreAnnotations getAnnotations() {
    this.complete();
    return this.annotations;
  }

  abstract void compute();

  public void complete() {
    this.annotations.complete();
  }

  public void toSreSet(final String annotate, final SreNamespace.Tag tag,
      final Set<String> set) {
    for (final String element : set) {
      this.annotations.appendAnnotation(annotate, tag,
          this.toSreElement(element));
    }
  }

  public void toSreSet(final String annotate, final SreNamespace.Tag tag,
      final ComponentsPositions positions) {
    for (final String element : positions) {
      this.annotations.appendAnnotation(annotate, tag,
          this.toSreElement(element));
    }
  }

  public SreElement toSreElement(final String name) {
    if (RichStructureHelper.isAtom(name)) {
      return new SreElement(SreNamespace.Tag.ATOM, name);
    }
    if (RichStructureHelper.isBond(name)) {
      return new SreElement(SreNamespace.Tag.BOND, name);
    }
    if (RichStructureHelper.isAtomSet(name)) {
      return new SreElement(SreNamespace.Tag.ATOMSET, name);
    }
    return new SreElement(SreNamespace.Tag.UNKNOWN, name);
  }

}
