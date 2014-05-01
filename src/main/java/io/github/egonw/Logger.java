package io.github.egonw;

import io.github.egonw.Cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.function.Consumer;

// Eventually inherit from Logger class. Currently I am too lazy.
public class Logger {
    public Boolean debug = false;
    public Boolean verbose = false;
    public PrintStream logFile = System.err;
    public PrintStream errFile = System.err;
    
    public Logger(Cli cli) {
        debug = cli.cl.hasOption("d");
        verbose = cli.cl.hasOption("v");
        openLogfile(cli, "l", (PrintStream stream) -> {logFile = stream;});
        openLogfile(cli, "x", (PrintStream stream) -> {errFile = stream;});
    }

    public void openLogfile (Cli cli, String optionName,
                             Consumer<PrintStream> logFile) {
        if (!cli.cl.hasOption(optionName)) {
                return;                
            }
        String fileName = cli.cl.getOptionValue(optionName);
        File file = new File(fileName);
        try {
	    logFile.accept(new PrintStream(file));
	}
	catch (IOException e) {
	    System.err.println("Error: Can't open logfile' " + fileName);
	}
    }

    public void error (String str) {
        if (debug) {
            this.errFile.print(str);
        }
    }

    public void logging (String str) {
        if (verbose) {
            this.logFile.print(str);
        }
    }

    public void finalize () {
        errFile.close();
	logFile.close();
    }
}
