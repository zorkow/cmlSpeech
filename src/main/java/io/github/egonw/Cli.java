package io.github.egonw;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {

    public CommandLine cl;

    public List<String> files = new ArrayList<String>();

    public Cli (String[] args) {
	parse(args);
    }

    public void parse( String[] args ) {
        Options options = new Options();
	// Basic Options
        options.addOption("help", false, "Print this message");
        options.addOption("d", "debug", false, "Debug mode");
	options.addOption("v", "verbose", false, "Verbose mode");
	// File Handling
	// options.addOption("i", "input", true, "Input File");
	options.addOption("o", "output", true, "Output file addition");
	options.addOption("l", "log", true, "Log File");
	options.addOption("x", "error", true, "Debug File");
	// Processing Options
        options.addOption("s", "subrings", false, "Compute subrings");
        options.addOption("sssr", "sssr", false, "Use SSSR method for sub-ring computation");
        options.addOption("vis", "visualize", false, "Visualize the abstraction graph");

        CommandLineParser parser = new BasicParser();
        try {
            this.cl = parser.parse(options, args);
        }
        catch (ParseException e) {
            usage(options, 1);
        }
        if (this.cl.hasOption("help")) {
            usage(options, 0);
        }

	for (int i = 0; i < this.cl.getArgList().size(); i++) {
	    String fileName = this.cl.getArgList().get(i).toString();
	    File f = new File(fileName);
	    if (f.exists() && !f.isDirectory()) {
		files.add(fileName);
	    } else {
		warning(fileName);
	    }
	}

    }

    private static void usage(Options options, int exitValue) {

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("enrich.sh", options);
        System.exit(exitValue);
    }

    private static void warning(String fileName) {
        System.err.println("Warning: File " 
                           + fileName + " does not exist. Ignored!");
    }
}
