package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParserLauncher {

    private final СlothesParser clothesParser;
    private final HouseholdParser householdParser;

    @Autowired
    public ParserLauncher(СlothesParser clothesParser, HouseholdParser householdParser) {
        this.clothesParser = clothesParser;
        this.householdParser = householdParser;
    }

    public void launchParsers() {
        clothesParser.parseCategory(); // Запуск асинхронно
        householdParser.parseCategory(); // Запуск асинхронно
    }
}
