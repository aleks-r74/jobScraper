package com.alexportfolio.jobScraper.service;

public interface ParserService {
    void removeOldCardsFromDB();
    void runParsing();
}
