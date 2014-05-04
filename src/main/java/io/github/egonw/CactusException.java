/**
 * @file   CactusException.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun May  4 14:48:23 2014
 * 
 * @brief  Exception for Cactus Utility class.
 * 
 * 
 */
package io.github.egonw;


/**
 * Exception for the Cactus Utility class to allow easy communication with
 * loggers.
 */

public class CactusException extends Exception {

    public CactusException(String message) {
	super(message);
    }

    public CactusException(String message, Throwable throwable) {
	super(message, throwable);
    }

}
