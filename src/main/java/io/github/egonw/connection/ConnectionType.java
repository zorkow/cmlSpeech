/**
 * @file   ConnectionType.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:08:08 2015
 * 
 * @brief  Enumerator for different connection types.
 * 
 * 
 */

//
package io.github.egonw.connection;

/**
 * Enum class for connection types.
 */

public enum ConnectionType {

    BRIDGEATOM ("bridgeAtom"),
    CONNECTINGBOND ("connectingBond"),
    SHAREDBOND ("sharedBond"),
    SHAREDATOM ("sharedAtom"),
    SPIROATOM ("spiroAtom"),
    ;

    public final String type;
    
    private ConnectionType (String type) {
            this.type = type;
    }

}
