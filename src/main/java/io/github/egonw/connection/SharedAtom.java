/**
 * @file   SharedAtom.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:27:03 2015
 * 
 * @brief  Class of shared atoms.
 * 
 * 
 */

//
package io.github.egonw.connection;

/**
 * Class of shared atoms.
 */

public class SharedAtom extends Connection {

    public SharedAtom(String connector, String connected) {
        super(connector, connected);
    }


    @Override
    public ConnectionType getType() {
        return ConnectionType.SHAREDATOM;
    }

}
