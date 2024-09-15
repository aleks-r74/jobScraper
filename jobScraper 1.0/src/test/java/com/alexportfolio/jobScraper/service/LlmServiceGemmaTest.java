package com.alexportfolio.jobScraper.service;

import com.alexportfolio.jobScraper.beans.TaskRunner;
import com.alexportfolio.jobScraper.parser.LinkedInParser;
import org.h2.util.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
///@SpringBootTest
class LlmServiceGemmaTest {
    @MockBean
    TaskRunner taskRunner;
    @MockBean
    List<LinkedInParser> workers;

    @Autowired
    @Qualifier("llmServiceGemma")
    LlmService llmService;

    //@Test
    void callLLM() {
        List<String> prompt = new ArrayList<>();
        prompt.add("You're my assistant.");
        prompt.add("Introduce yourself");
        var response = llmService.callLLM(prompt);
        assert(response.isPresent());
        System.out.println(response.get());
    }
}