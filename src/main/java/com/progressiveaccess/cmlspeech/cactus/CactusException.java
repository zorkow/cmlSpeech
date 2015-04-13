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
 * @file   CactusException.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun May  4 14:48:23 2014
 *
 * @brief  Exception for Cactus Utility class.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

/**
 * Exception for the Cactus classes. To allow passing through closures it is
 * unchecked!
 */
public class CactusException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Exception in Cactus call threads.
   *
   * @param message
   *          The error message.
   */
  public CactusException(final String message) {
    super(message);
  }

}