/**
 * @file   SpiroAtom.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:27:03 2015
 * 
 * @brief  Class of spiro atoms.
 * 
 * 
 */

//
package io.github.egonw.connection;

/**
 * Class of spiro atoms.
 */

public class SpiroAtom extends Connection {

    public SpiroAtom(String connector, String connected) {
        super(connector, connected);
    }


    @Override
    public ConnectionType getType() {
        return ConnectionType.SPIROATOM;
    }

}
