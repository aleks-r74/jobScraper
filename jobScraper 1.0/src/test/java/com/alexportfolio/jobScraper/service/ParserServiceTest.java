package com.alexportfolio.jobScraper.service;

import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.repository.JobRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {ParserConfig.class, WorkersConfig.class})
@SpringBootTest
class ParserServiceTest {

    @Autowired
    ParserServiceImpl parserService;
    @Autowired
    JobRepository jobRepository;

    //@Test
    @Disabled
    void runPlainParsingTest() {
        parserService.runJobTitleParsing();
        parserService.runJobDescriptionParsing();

        StringBuilder str = new StringBuilder();
        for(JobCard card: parserService.storage){
            str.append(card.toString());
        }
        try {
            Files.writeString(Path.of("./src/test/output/ParsingTestOutput.txt"),str.toString(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //assert that description is not blank
        var randomDescription = parserService.storage.stream().findAny().get().getDescription();
        assert(!randomDescription.isBlank());
    }

    //@Test
    @Disabled
    void OneCycleTest(){
        var before = jobRepository.count();
        System.out.printf("Befor parsing, db has %d entries\n", before);
        parserService.runParsing();
        var after = jobRepository.count();
        System.out.printf("After parsing, db has %d entries\n", after);
        assert(before<after);

        var iterator = jobRepository.findAll().iterator();
        while(iterator.hasNext()){
            var card = iterator.next();
            assert(
                   !card.getTitle().isBlank() &&
                   !card.getCompany().isBlank() &&
                   !card.getDescription().isBlank() &&
                   !card.getUrl().isBlank() &&
                    card.getTimeStamp()!=null
                    );
        }

    }
    //@Test
    @Disabled
    void runParsingTestOutputEachStep(){
        // load title & company name from DB
        parserService. loadStorageProjection();
        System.out.printf("Storage projection has %d records\n", parserService.storageProjection.size());
        // gathering job cards to storage
        parserService.runJobTitleParsing();
        // remove from storage those cards that present in storageProjection
        System.out.printf("Found %d job titles\n", parserService.storage.size());
        parserService.storage.removeAll(parserService.storageProjection);
        System.out.printf("Storage size after removing projections: %d\n", parserService.storage.size());
        // parse description for remaining in storage
        parserService.runJobDescriptionParsing();
        // persist parsed cards to DB
        jobRepository.saveAll(parserService.storage);
    }

}