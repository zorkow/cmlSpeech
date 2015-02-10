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

