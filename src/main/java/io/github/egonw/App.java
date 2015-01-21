package io.github.egonw;

import io.github.egonw.Cli;
import io.github.egonw.CMLEnricher;
import io.github.egonw.Logger;

public class App {

    public static void main(String[] args) throws Exception {
	Cli.parse(args);
	Logger logger = new Logger();
	if (!Cli.files.isEmpty()) {
	    CMLEnricher cmle = new CMLEnricher(logger);
	    cmle.enrichFiles();
	}
	logger.finalize();
    }
}

