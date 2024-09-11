package com.alexportfolio.webFace.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//@SpringBootTest
class RestServiceTest {
    @Autowired
    RestService restService;

    @Disabled
    @Test
    void saveAndGetLinks() throws IOException {
        List<String> list = restService.getLinks();
        assert(list.size()>0);
        restService.saveLinks(list.stream().collect(Collectors.joining()));
    }

    @Disabled
    @Test
    void saveAndGetKeywords() throws IOException {
        String saveKeywords = "save1, save2";
        String stopKeywords = "stop1, stop2";
        restService.saveKeywords(stopKeywords, saveKeywords);
        var keywords = restService.getKeywords();
        assert(keywords.getProperty("saveKeywords").equals(saveKeywords));
        assert(keywords.getProperty("stopKeywords").equals(stopKeywords));

    }
    @Disabled
    @Test
    void writeAndReadResume() throws IOException {
        String str = "test";
        restService.saveResume(str);
        assert(str.equals(restService.getResume()));
    }

}