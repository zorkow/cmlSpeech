/**
 * @file   ConnectionComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 10 17:54:24 2015
 * 
 * @brief  A comparator for connections.
 * 
 * 
 */

//
package io.github.egonw.connection;

import io.github.egonw.base.CMLNameComparator;

import java.util.Comparator;

/**
 * Comparison of connections currently implemented on their type identifier.
 */

public class ConnectionComparator implements Comparator<Connection> {
    
    public int compare(Connection con1, Connection con2) {
        if (con1.getType().equals(con2.getType())) {
            Comparator<String> comp = new CMLNameComparator();
            Integer comparison = comp.compare(con1.getConnector(), con2.getConnector());
            if (comparison == 0) {
                return comp.compare(con1.getConnected(), con2.getConnected());
            }
            return comparison;
        }
        if (con1.getType().equals(ConnectionType.SPIROATOM) ||
            (con1.getType().equals(ConnectionType.BRIDGEATOM) &&
             !con2.getType().equals(ConnectionType.SPIROATOM)) ||
            (con1.getType().equals(ConnectionType.SHAREDATOM) &&
             !con2.getType().equals(ConnectionType.BRIDGEATOM) &&
             !con2.getType().equals(ConnectionType.SPIROATOM)) || 
            (con1.getType().equals(ConnectionType.SHAREDBOND) &&
             !con2.getType().equals(ConnectionType.BRIDGEATOM) &&
             !con2.getType().equals(ConnectionType.SHAREDATOM) &&
             !con2.getType().equals(ConnectionType.SPIROATOM))) {
            return -1;
        }
        return 1;
    }

}
