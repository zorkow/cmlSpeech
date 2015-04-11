/**
 * @file   XMLAnnotations.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Thu Apr  9 12:23:40 2015
 * 
 * @brief  Interface for XML annotations output.
 * 
 * 
 */

//
package io.github.egonw.sre;

/**
 * The basic XML annotations every enriched object should produce.
 */

public interface XMLAnnotations {

    public SreNamespace.Tag tag ();

    public SreElement annotation ();

}
