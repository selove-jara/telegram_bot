package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Сервис для запуска парсеров.
 * Используется для централизованного управления запуском парсинга категорий товаров.
 */
@Service
public class ParserLauncher {

    private final СlothesParser clothesParser;
    private final HouseholdParser householdParser;

    @Autowired
    public ParserLauncher(СlothesParser clothesParser, HouseholdParser householdParser) {
        this.clothesParser = clothesParser;
        this.householdParser = householdParser;
    }

    /**
     * Метод запускает все зарегистрированные парсеры.
     * Метод вызывает асинхронные методы `parseCategory()` у каждого парсера.
     */
    public void launchParsers() {
        clothesParser.parseCategory(); // Запуск асинхронно
        householdParser.parseCategory(); // Запуск асинхронно
    }
}
