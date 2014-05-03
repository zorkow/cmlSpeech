package io.github.egonw;

import io.github.egonw.Cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.function.Consumer;

// Eventually inherit from Logger class. Currently I am too lazy.
public class Logger {
    public Boolean debug = false;
    public Boolean verbose = false;
    public PrintWriter logFile = new PrintWriter(System.err);
    public PrintWriter errFile = new PrintWriter(System.err);
    
    public Logger(Cli cli) {
        debug = cli.cl.hasOption("d");
        verbose = cli.cl.hasOption("v");
        openLogfile(cli, "l", (PrintWriter stream) -> {logFile = stream;});
        openLogfile(cli, "x", (PrintWriter stream) -> {errFile = stream;});
    }

    public void openLogfile (Cli cli, String optionName,
                             Consumer<PrintWriter> logFile) {
        if (!cli.cl.hasOption(optionName)) {
                return;                
            }
        String fileName = cli.cl.getOptionValue(optionName);
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
