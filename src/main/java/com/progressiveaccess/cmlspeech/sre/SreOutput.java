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
 * @file   SreOutput.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Jun 19 16:34:40 2014
 *
 * @brief  Class to handle SRE annotations.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;

/**
 * Constructs structural annotations for Sre.
 */

public class SreOutput extends SreXml {

  public SreOutput() {
    super();
    this.compute();
  }

  @Override
  public void compute() {
    RichStructureHelper.getAtoms().stream().forEach(
         a -> this.annotations.registerAnnotation(a.getId(), a.annotation()));
    RichStructureHelper.getBonds().stream().forEach(
         a -> this.annotations.registerAnnotation(a.getId(), a.annotation()));
    RichStructureHelper .getAtomSets().stream().forEach(
         a -> this.annotations.registerAnnotation(a.getId(), a.annotation()));
  }

}
