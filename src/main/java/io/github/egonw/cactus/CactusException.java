/**
 * @file   CactusException.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun May  4 14:48:23 2014
 * 
 * @brief  Exception for Cactus Utility class.
 * 
 * 
 */
package io.github.egonw.cactus;


/**
 * Exception for the Cactus classes.
 * To allow passing through closures it is unchecked!
 */

public class CactusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CactusException(String message) {
	super(message);
    }

    public CactusException(String message, Throwable throwable) {
	super(message, throwable);
    }

}
