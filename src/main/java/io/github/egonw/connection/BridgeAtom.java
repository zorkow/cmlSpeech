/**
 * @file   BridgeAtom.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:27:03 2015
 * 
 * @brief  Class of bridge atoms.
 * 
 * 
 */

//
package io.github.egonw.connection;

/**
 * Class of bridge atoms.
 */

public class BridgeAtom extends Connection {

    public BridgeAtom(String connector, String connected) {
        super(connector, connected);
    }


    @Override
    public ConnectionType getType() {
        return ConnectionType.BRIDGEATOM;
    }

}
