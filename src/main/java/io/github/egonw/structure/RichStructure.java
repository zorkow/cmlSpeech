/**
 * @file   RichStructure.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Jun 10 21:37:18 2014
 * 
 * @brief  Interface specification for enriched structures.
 * 
 * 
 */

//
package io.github.egonw.structure;

import io.github.egonw.connection.Connection;

import java.util.Set;

/**
 *
 */

public interface RichStructure<S> {
 
    
    /**
     * @return The list of components of this structure.
     */
    Set<String> getComponents();


    /**
     * @return The list of contexts of this structure.
     */
    Set<String> getContexts();


    /**
     * @return The list of external bonds of this structure.
     */
    Set<String> getExternalBonds();


    /**
     * @return The list of connections of this structure.
     */
    Set<Connection> getConnections();


    /**
     * @return The list of direct Super-Systems.
     */
    Set<String> getSuperSystems();


    /**
     * @return The list of direct Sub-Systems. These can also be atoms.
     */
    Set<String> getSubSystems();


    /**
     * Returns the ID of the structure.
     * @return ID string.
     */
    String getId();

    
    /**
     * The structure embedded in this enriched object.
     * @return Un-enriched structure.
     */
    S getStructure();

}
