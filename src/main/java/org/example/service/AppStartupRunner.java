package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements CommandLineRunner {

    private final ParserLauncher parserLauncher;

    @Autowired
    public AppStartupRunner(ParserLauncher parserLauncher) {
        this.parserLauncher = parserLauncher;
    }

    @Override
    public void run(String... args) throws Exception {
        parserLauncher.launchParsers();
    }
}
