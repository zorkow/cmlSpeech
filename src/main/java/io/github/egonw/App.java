package io.github.egonw;

import io.github.egonw.Cli;
import io.github.egonw.CMLEnricher;
import io.github.egonw.Logger;

public class App {

    public static void main(String[] args) throws Exception {
	Cli cli = new Cli(args);
	Logger logger = new Logger(cli);
	if (!cli.files.isEmpty()) {
	    CMLEnricher cmle = new CMLEnricher(cli, logger);
	    cmle.enrichFiles();
	}
	logger.finalize();
    }
}

