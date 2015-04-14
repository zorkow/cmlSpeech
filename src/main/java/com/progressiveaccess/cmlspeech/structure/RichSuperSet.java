/**
 * @file   RichSuperStructure.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Mar 22 02:54:39 2015
 *
 * @brief  Superset interface.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

/**
 * Interface for all structures that contain proper subsystems, i.e., not only
 * single atoms as components.
 */

public interface RichSuperSet extends RichSet {

  /**
   * @return The order of the subsystems.
   */
  ComponentsPositions getPath();

  /**
   * @return Computes the order of the subsystems.
   */
  void setPath();

}
