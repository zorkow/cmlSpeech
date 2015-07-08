/**
 * @file   Bridge.java
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">
 *          Volker Sorge</a>
 * @date   Mon Jun 29 11:23:22 2015
 *
 * @brief  Bridge connections for sub rings.
 *
 */

//

package com.progressiveaccess.cmlspeech.connection;

import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.XmlVisitor;

import java.util.SortedSet;

/**
 * Bridge for a neighbouring fused ring is a combination of shared bonds and
 * bridge atoms.
 */

public class Bridge extends Connection {

  private SortedSet<Connection> bridges;

  /**
   * Constructs a bridge.
   *
   * @param connectors Set of bridges.
   * @param connected Name of connected structure.
   */
  public Bridge(final SortedSet<Connection> connectors,
                final String connected) {
    super(connectors.first().getConnector(), connected);
    bridges = connectors;
  }


  /**
   * @return Set of shared bonds and bridge atoms.
   */
  public SortedSet<Connection> getBridges() {
    return bridges;
  }


  @Override
  public ConnectionType getType() {
    return ConnectionType.BRIDGE;
  }

  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.BRIDGE;
  }

  @Override
  public void accept(final XmlVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    String result = "\nBridge to " + this.getConnected() + " via:";
    for (Connection bridge : this.getBridges()) {
      result += bridge.toString();
    }
    return result;
  }

}
