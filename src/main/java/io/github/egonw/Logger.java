package io.github.egonw;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.function.Consumer;

// TODO: Eventually inherit from Logger class. Currently I am too lazy.
/** Description for Logger. */
public class Logger {
    public Boolean debug = false;
    public Boolean verbose = false;
    public PrintWriter logFile = new PrintWriter(System.err);
    public PrintWriter errFile = new PrintWriter(System.err);
    
    public Logger() {
        debug = Cli.hasOption("d");
        verbose = Cli.hasOption("v");
        openLogfile("l", (PrintWriter stream) -> {logFile = stream;});
        openLogfile("x", (PrintWriter stream) -> {errFile = stream;});
    }

    public void openLogfile (String optionName, Consumer<PrintWriter> logFile) {
        if (!Cli.hasOption(optionName)) {
                return;                
            }
        String fileName = Cli.getOptionValue(optionName);
        File file = new File(fileName);
        try {
	    logFile.accept(new PrintWriter(file));
	}
	catch (IOException e) {
	    System.err.println("Error: Can't open logfile' " + fileName);
	}
    }

    public void error (Object str) {
        if (debug) {
            this.errFile.print(str);
        }
    }

    public void logging (Object str) {
        if (verbose) {
            this.logFile.print(str);
        }
    }

    public void finalize () {
        errFile.close();
	logFile.close();
    }
}
