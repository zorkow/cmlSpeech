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
 * @file   SpeechAnnotations.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Apr  9 12:23:40 2015
 *
 * @brief  Interface for Speech annotations output.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

/**
 * The basic Speech annotations every enriched object should produce.
 */

public interface SpeechAnnotations {

  /**
   * @return A short simple description for the object.
   */
  String shortSimpleDescription();


  /**
   * @return A short expert description for the object. Defaults to simple
   *     description.
   */
  default String shortExpertDescription() {
    return shortSimpleDescription();
  }


  /**
   * @return A long simple description for the object. Defaults to short
   *     description.
   */
  default String longSimpleDescription() {
    return shortSimpleDescription();
  }


  /**
   * @return A long expert description for the object. Defaults to simple
   *     description.
   */
  default String longExpertDescription() {
    return longSimpleDescription();
  }

}
