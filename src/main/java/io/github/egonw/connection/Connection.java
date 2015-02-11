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
package io.github.egonw.connection;


/**
 * Connections consist of
 * -- a type string: shared atom, shared bond, connecting bond
 * -- the connecting structure: name of either bond or atom
 * -- the connected structure: name of an atom or an atom set
 */

public class Connection extends ConnectionComparator implements Comparable<Connection> {
    
    private ConnectionType type;
    private String connector = "";
    private String connected = "";

    public Connection() {
    }

    public Connection(ConnectionType type, String connector, String connected) {
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
    
    public ConnectionType getType() {
        return this.type;
    }
    
    public boolean hasType(ConnectionType type) {
        return type.equals(this.type);
    }

    @Override
    public String toString() {
        return this.getConnector() + " -> " + this.getConnected();
    }

    public int compareTo(Connection con) {
        return compare(this, con);
    }

}
