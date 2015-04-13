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
 * @file   RichSetType.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Feb 11 00:08:08 2015
 *
 * @brief  Enumerator for different richSet types.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

/**
 * Enum class for rich set types.
 */

public enum RichSetType {

  ALIPHATIC("Aliphatic chain"),
  FUSED("Fused ring"),
  ISOLATED("Isolated ring"),
  SMALLEST("Subring"),
  MOLECULE("Molecule"),
  FUNCGROUP("Functional Group"), ;

  public final String name;

  private RichSetType(final String type) {
    this.name = type;
  }
}
