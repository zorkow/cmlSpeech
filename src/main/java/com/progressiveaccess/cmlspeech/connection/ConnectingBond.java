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
 * @file   ConnectingBond.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Feb 11 00:27:03 2015
 *
 * @brief  Class of connecting bonds.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.connection;

import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.XmlVisitor;

/**
 * Class of connecting bonds.
 */

public class ConnectingBond extends Connection {

  private String origin;

  /**
   * Constructs a connecting bond.
   *
   * @param connector
   *          Name of connecting structure.
   * @param connected
   *          Name of connected structure.
   * @param origin
   *          Name of atom the bond originates in.
   */
  public ConnectingBond(final String connector, final String connected,
                        final String origin) {
    super(connector, connected);
    this.origin = origin;
  }


  /**
   * @return The atom the bond originates in.
   */
  public String getOrigin() {
    return this.origin;
  }

  @Override
  public ConnectionType getType() {
    return ConnectionType.CONNECTINGBOND;
  }

  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.CONNECTINGBOND;
  }

  @Override
  public String toString() {
    return "\n" + this.getType() + ": " + this.getOrigin() + " -> "
        + this.getConnector() + " -> " + this.getConnected();
  }


  @Override
  public void accept(final XmlVisitor visitor) {
    visitor.visit(this);
  }

}
