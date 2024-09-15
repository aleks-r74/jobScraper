package com.alexportfolio.jobScraper;

import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.parser.LinkedInParser;
import com.alexportfolio.jobScraper.parser.ParserUtils;
import com.alexportfolio.jobScraper.repository.JobRepository;
import com.alexportfolio.jobScraper.service.FileService;
import com.alexportfolio.jobScraper.service.ParserService;
import com.alexportfolio.jobScraper.service.ParserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


import static org.assertj.core.api.Assertions.assertThat;

public class NoContextTests {
    @MockBean
    List<LinkedInParser> workers;

    //@Test
    @DisplayName("Checking Set<JobCard> has only unique JobCards")
    void uniqueCardTest(){
        Set<JobCard> uniqueCardSet = new HashSet<>();
        for(int i=0;i<5;i++)
        uniqueCardSet.add(
               new JobCard("title","company","description","url","notes", LocalDateTime.now(), null)
        );
        assertThat(uniqueCardSet.size()).isEqualTo(1);
    }
    //@Test
    @DisplayName("Checking ParserUtils.loadXpathProperties()")
    void loadXpathPropertiesTest() throws IOException {
        new ParserUtils().loadXpathProperties();
        assert(ParserUtils.xpaths.size()!=0);
        for(String xpath: ParserUtils.xpaths.values()){
            assertThat(xpath).isNotNull().isNotEmpty();
        }
    }


    //@ParameterizedTest
    @CsvSource({
            "33, 3", //equal chunks
            "11, 1", // one chunk
            "20, 3", // not equal chunks
            "1, 10" // chunks more than items
    })
    void splitListTest(int itemsCount, int chunksCount){
        var instance = new ParserServiceImpl(null, workers, null);
        var list = Stream.iterate(0,s->s+1).limit(itemsCount).toList();
        var splittedList = instance.splitList(list, chunksCount);
        if(itemsCount>chunksCount) {
            assert (splittedList.size() == chunksCount);
            assert (splittedList.get(0).size() == Math.ceil((double) itemsCount / chunksCount));
        } else{
            assert (splittedList.size() == 1);
            assert (splittedList.get(0).size() == itemsCount);
        }
    }
    //@Test
    @DisplayName("Checking ParserUtils.getFilteringKeywords()")
    void getFilteringKeywordsTest() throws IOException {
        var parserUtils = new ParserUtils();
        var fs = new FileService();
        fs.init();
        parserUtils.setFileService(fs);
        var filteringKeywords = parserUtils.getFilteringKeywords();

        for(String keyword: filteringKeywords.get("stop")){
            System.out.println("stop: "+keyword);
            assertThat(keyword).isNotNull().isNotEmpty();
        }
        for(String keyword: filteringKeywords.get("save")){
            System.out.println("save: "+keyword);
            assertThat(keyword).isNotNull().isNotEmpty();
        }
    }
    //@Test
    void saveObjectLoadObjectTest() throws IOException {
        String fn = "src/test/output/testObj.ser";
        ParserUtils utils = new ParserUtils();
        var mySet = Set.of("One","Two","Three");
        utils.saveObject(mySet, fn);
        var fromFile = utils.<Set<String>>loadObject(fn);
        if(fromFile instanceof Set fileSet){
            fileSet.forEach(System.out::println);
            assert(fileSet.size()>0);
        }
    }

    //@Test
    void readRoles() {
        var fileService = new FileService();
        fileService.init();
        var roles = fileService.getRoles();
        assert(!roles.isEmpty());
        System.out.println(roles);

    }
}
