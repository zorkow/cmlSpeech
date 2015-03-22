/**
 * @file   RichSuperStructure.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun Mar 22 02:54:39 2015
 * 
 * @brief  Superset interface.
 * 
 * 
 */

//
package io.github.egonw.structure;


/**
 * Interface for all structures that contain proper subsystems, i.e.,
 * not only single atoms as components.
 */

public interface RichSuperSet extends RichSet {

    /**
     * @return The order of the subsystems.
     */
    public ComponentsPositions getPath();
    
    /**
     * @return Computes the order of the subsystems.
     */
    public void setPath();

}
