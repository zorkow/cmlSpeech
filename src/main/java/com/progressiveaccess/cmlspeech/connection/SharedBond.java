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
 * @file   SharedBond.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Feb 11 00:27:03 2015
 *
 * @brief  Class of shared bonds.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.connection;

import com.progressiveaccess.cmlspeech.sre.SreElement;
import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.XmlVisitor;

/**
 * Class of shared bonds.
 */

public class SharedBond extends Connection {

  /**
   * Constructs a shared bond.
   *
   * @param connector Name of connecting structure.
   * @param connected Name of connected structure.
   */
  public SharedBond(final String connector, final String connected) {
    super(connector, connected);
  }

  @Override
  public ConnectionType getType() {
    return ConnectionType.SHAREDBOND;
  }

  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.SHAREDBOND;
  }

  @Override
  public SreElement annotation() {
    return new SreElement(this.tag(), new SreElement(SreNamespace.Tag.BOND,
        this.getConnector()), new SreElement(SreNamespace.Tag.ATOMSET,
            this.getConnected()));
  }

  @Override
  public void accept(final XmlVisitor visitor) {
    visitor.visit(this);
  }

}
