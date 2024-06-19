package com.alexportfolio.jobScraper.service;

import java.util.List;
import java.util.Optional;

public interface LlmService {

    Optional<String> callLLM(List<String> prompt);
    String getName();
}
