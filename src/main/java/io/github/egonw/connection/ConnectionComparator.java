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
