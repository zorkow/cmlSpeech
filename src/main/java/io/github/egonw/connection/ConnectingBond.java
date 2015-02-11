/**
 * @file   ConnectingBond.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:27:03 2015
 * 
 * @brief  Class of connecting bonds.
 * 
 * 
 */

//
package io.github.egonw.connection;

/**
 * Class of connecting bonds.
 */

public class ConnectingBond extends Connection {

    public ConnectingBond(String connector, String connected) {
        super(connector, connected);
    }


    @Override
    public ConnectionType getType() {
        return ConnectionType.CONNECTINGBOND;
    }

}
