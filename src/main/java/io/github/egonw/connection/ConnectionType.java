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
 * @file   ConnectionType.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Feb 11 00:08:08 2015
 * 
 * @brief  Enumerator for different connection types.
 * 
 * 
 */

//

package io.github.egonw.connection;

/**
 * Enum class for connection types.
 */

public enum ConnectionType {

  BRIDGEATOM("bridgeAtom"),
  CONNECTINGBOND("connectingBond"),
  SHAREDBOND("sharedBond"),
  SHAREDATOM("sharedAtom"),
  SPIROATOM("spiroAtom"), ;

  public final String type;

  private ConnectionType(String type) {
    this.type = type;
  }

}
