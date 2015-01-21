package io.github.egonw;

public class App {

    public static void main(String[] args) throws Exception {
	Cli.init(args);
	Logger logger = new Logger();
	if (!Cli.getFiles().isEmpty()) {
	    CMLEnricher cmle = new CMLEnricher(logger);
	    cmle.enrichFiles();
	}
	logger.finalize();
    }
}

