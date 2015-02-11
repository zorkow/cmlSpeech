/**
 * @file   SharedBond.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:27:03 2015
 * 
 * @brief  Class of shared bonds.
 * 
 * 
 */

//
package io.github.egonw.connection;

/**
 * Class of shared bonds.
 */

public class SharedBond extends Connection {

    public SharedBond(String connector, String connected) {
        super(connector, connected);
    }


    @Override
    public ConnectionType getType() {
        return ConnectionType.SHAREDBOND;
    }

}
