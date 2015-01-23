package io.github.egonw;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;

// TODO: Eventually inherit from Logger class. Currently I am too lazy.
/** Description for Logger. */
public class Logger {
    private static Boolean debug = false;
    private static Boolean verbose = false;
    private static PrintWriter logFile = new PrintWriter(System.err);
    private static PrintWriter errFile = new PrintWriter(System.err);
    
    protected Logger() { }

    public static void start() {
        debug = Cli.hasOption("d");
        verbose = Cli.hasOption("v");
        openLogfile("l", (PrintWriter stream) -> {logFile = stream;});
        openLogfile("x", (PrintWriter stream) -> {errFile = stream;});
    }

     private static void openLogfile (String optionName, Consumer<PrintWriter> logFile) {
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

    public static void error (Object str) {
        if (debug) {
            Logger.errFile.print(str);
        }
    }

    public static void logging (Object str) {
        if (verbose) {
            Logger.logFile.print(str);
        }
    }

    public static void end () {
        Logger.errFile.close();
	Logger.logFile.close();
    }
}
