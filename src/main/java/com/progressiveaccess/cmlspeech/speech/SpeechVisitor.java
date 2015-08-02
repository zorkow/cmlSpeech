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
 * @file   SpeechVisitor.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Jul 30 19:12:08 2015
 *
 * @brief  Interface for all speech visitors.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import com.progressiveaccess.cmlspeech.sre.XmlVisitor;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;

/**
 * Speech visitor interface.
 */

public interface SpeechVisitor extends XmlVisitor {

  /**
   * Sets the components position mapping to be used in the given
   * translation.
   *
   * @param positions
   *          The components to positions mappings.
   */
  void setContextPositions(final ComponentsPositions positions);


  /**
   * @return The position mapping currently in use.
   */
  ComponentsPositions getContextPositions();


  /**
   * @return The computed speech string.
   */
  String getSpeech();

}
