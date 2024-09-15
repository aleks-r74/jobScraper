package com.alexportfolio.jobScraper.parser;


import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.repository.JobRepository;
import com.alexportfolio.jobScraper.service.FileService;
import com.alexportfolio.jobScraper.service.ParserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//@ExtendWith(SpringExtension.class)
//@TestPropertySource(locations = "classpath:application.properties")
public class LinkedInParserTest {
    @Autowired
    List<LinkedInParser> workers;
    @MockBean
    JobRepository jobRepository;
    @Autowired
    Environment env;
    @Autowired
    FileService fs;


    //@Test
    @Disabled
    void loginTest() throws IOException {
        LinkedInParser page = workers.get(0);
        assert(page.driver.getCurrentUrl().toLowerCase().contains("feed"));
    }

    //@Test
    @DisplayName("Job cards parsing test")
    void jobParsingTest() throws IOException {
        LinkedInParser page = workers.get(0);
        for(String link: fs.getLinks()){
            int cardsParsed = page.parseJobCards(link).size();
            System.out.printf("Parsed %d cards\n",cardsParsed);
            assert(cardsParsed>0);
        }
    }

    //@Test
    @Disabled
    @DisplayName("job description parsing test")
    void fillDescriptionTest() throws IOException {
        LinkedInParser page = workers.get(0);
        var links = Files.readAllLines(Path.of("./src/test/resources/job_cards_url_testdata.txt"));
        for(String url: links){
            var card = new JobCard("title","company","description",url,"notes", LocalDateTime.now(),null );
            var result = page.fillDescription(List.of(card));
            var list = List.copyOf(result);
            System.out.printf("Job description parsed, has length %d signs\n", list.getFirst().getDescription().length());
            assert(list.getFirst().getDescription().length()>10);
        }
    }


}
