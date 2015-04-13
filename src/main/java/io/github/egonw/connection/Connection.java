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
 * @file   Connection.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jun 11 12:18:00 2014
 *
 * @brief Class of connection structures. These are effectively triples of
 *     strings.
 *
 *
 */

//

package io.github.egonw.connection;

import io.github.egonw.sre.SreElement;
import io.github.egonw.sre.SreNamespace;
import io.github.egonw.sre.XmlAnnotations;

/**
 * Connections consist of -- the connecting structure: name of either bond or
 * atom -- the connected structure: name of an atom or an atom set.
 */

public abstract class Connection extends ConnectionComparator implements
Comparable<Connection>, XmlAnnotations {

  private String connector = "";
  private String connected = "";

  public Connection(final String connector, final String connected) {
    this.connector = connector;
    this.connected = connected;
  }

  public String getConnector() {
    return this.connector;
  }

  public String getConnected() {
    return this.connected;
  }

  public abstract ConnectionType getType();

  public boolean hasType(final ConnectionType type) {
    return type.equals(this.getType());
  }

  @Override
  public String toString() {
    return "\n" + this.getType() + ": " + this.getConnector() + " -> "
        + this.getConnected();
  }

  @Override
  public int compareTo(final Connection con) {
    return this.compare(this, con);
  }

  @Override
  public abstract SreNamespace.Tag tag();

  @Override
  public SreElement annotation() {
    return new SreElement(this.tag(), new SreElement(SreNamespace.Tag.ATOM,
        this.getConnector()), new SreElement(SreNamespace.Tag.ATOMSET,
            this.getConnected()));
  }

}
