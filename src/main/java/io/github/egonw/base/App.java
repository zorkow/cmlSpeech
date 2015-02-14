/**
 * @file   App.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:05:04 2015
 * 
 * @brief  Basci application file for project.
 * 
 * 
 */


//
package io.github.egonw.base;

public class App {

    public static void main(String[] args) throws Exception {
	Cli.init(args);
	Logger.start();
	if (!Cli.getFiles().isEmpty()) {
	    CMLEnricher cmle = new CMLEnricher();
	    cmle.enrichFiles();
	}
	Logger.end();
    }
}

