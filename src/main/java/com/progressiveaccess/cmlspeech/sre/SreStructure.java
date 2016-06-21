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
 * @file   SreStructure.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Apr 26 12:25:01 2015
 *
 * @brief  Generates the exploration structure.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;

/**
 * Client for the structure visitor.
 */

public class SreStructure {

  private StructureVisitor visitor = new StructureVisitor();

  /** Constructor. */
  public SreStructure() {
    this.compute();
  }


  /**
   * Computes the annotation.
   */
  public void compute() {
    RichStructureHelper.getAtoms().stream()
      .forEach(a -> a.accept(this.visitor));
    RichStructureHelper.getBonds().stream()
      .forEach(a -> a.accept(this.visitor));
    RichStructureHelper.getAtomSets().stream()
      .forEach(a -> a.accept(this.visitor));
  }


  /**
   * @return The annotation.
   */
  public SreAnnotations getAnnotations() {
    SreAnnotations annotations = visitor.getAnnotations();
    annotations.complete();
    return annotations;
  }

}
