/**
 * @file   Connection.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Jun 11 12:18:00 2014
 * 
 * @brief  Class of connection structures. These are effectively triples of strings.
 * 
 * 
 */

//
package io.github.egonw;

import java.util.Comparator;

/**
 * Connections consist of
 * -- a type string: shared atom, shared bond, connecting bond
 * -- the connecting structure: name of either bond or atom
 * -- the connected structure: name of an atom or an atom set
 */

public class Connection implements Comparator<Connection>, Comparable<Connection> {
    
    public enum Type {
        CONNECTINGBONDS ("connectingBond"),
        SHAREDBOND ("sharedBond"),
        SHAREDATOM ("sharedAtom"),
        ;

        public final String type;
        
        private Type (String type) {
            this.type = type;
        }

    }

    private Type type;
    private String connector = "";
    private String connected = "";

    public Connection() {
    }

    public Connection(Type type, String connector, String connected) {
        this.type = type;
        this.connector = connector;
        this.connected = connected;
    }

    public String getConnector() {
        return this.connector;
    }
    
    public String getConnected() {
        return this.connected;
    }
    
    public boolean hasType(Type type) {
        return type.equals(this.type);
    }

    public int compare(Connection con1, Connection con2) {
        if (con1.type.equals(con2.type)) {
            Comparator comp = new CMLNameComparator();
            return comp.compare(con1.getConnector(), con2.getConnector());
        }
        if (con1.type.equals(Connection.Type.SHAREDATOM) || 
            (con1.type.equals(Connection.Type.SHAREDBOND) && 
             !con2.type.equals(Connection.Type.SHAREDATOM))) {
            return -1;
        }
        return 1;
    }

    public int compareTo(Connection con) {
        return compare(this, con);
    }


    @Override
    public String toString() {
        return this.getConnector() + " -> " + this.getConnected();
    }

}
