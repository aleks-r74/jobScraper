package com.alexportfolio.jobScraper.service;

import com.alexportfolio.jobScraper.beans.TaskRunner;
import com.alexportfolio.jobScraper.daemon.JobDescriptionSummarizationDaemon;
import com.alexportfolio.jobScraper.parser.LinkedInParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

//@SpringBootTest
class JobDescriptionSummarizationServiceImplTest {
    @MockBean
    TaskRunner taskRunner;
    @MockBean
    List<LinkedInParser> workers;

    @Autowired
    JobDescriptionSummarizationDaemon summarizationService;

    //@Test
    void run() throws InterruptedException {
        Thread.sleep(60000);
    }

}