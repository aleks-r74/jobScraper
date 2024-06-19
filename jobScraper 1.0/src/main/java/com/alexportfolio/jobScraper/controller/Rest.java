package com.alexportfolio.jobScraper.controller;

import com.alexportfolio.jobScraper.JobScraperApplication;
import com.alexportfolio.jobScraper.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/settings")
public class Rest {
    @Autowired
    FileService fileService;

    @GetMapping("/links")
    List<String> getLinks(){
        List<String> links = fileService.readLinks();
        return links;
    }

    @PutMapping(path="/links", consumes = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> setLinks(@RequestBody(required = false) String links){
        ResponseEntity<String> resp;
        try{
            if(links==null) links = "";
            // happy path
            fileService.saveLinks(links.strip());
            resp = ResponseEntity.status(HttpStatus.OK).build();
        } catch(RuntimeException e){
            resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return resp;
    }

    @GetMapping("/keywords")
    Map<String,String> getKeywords(){
        var prop = fileService.getKeywords();
        return Map.of("stopKeywords", (String) prop.get("stopKeywords"), "saveKeywords", (String) prop.get("saveKeywords"));
    }

    @PutMapping(path="/keywords", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> setKeywords(@RequestBody Map<String,String> keywords){
        ResponseEntity<String> resp;
        try{
            fileService.saveKeywords(keywords.get("stopKeywords"),keywords.get("saveKeywords"));
            resp = ResponseEntity.status(HttpStatus.OK).build();
        } catch(RuntimeException e){
            resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return resp;
    }

    @GetMapping("/resume")
    String getResume(){
        return fileService.getResume();
    }

    @PutMapping(path="/resume", consumes = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity setResume(@RequestBody(required = false) String resume){
        ResponseEntity resp;
        if(resume==null) resume = "";
        try{
            // happy path
            fileService.saveResume(resume);
            resp = ResponseEntity.status(HttpStatus.OK).build();
        } catch(RuntimeException e){
            resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return resp;
    }

    @PostMapping("/shutdown")
    @ResponseStatus(HttpStatus.OK)
    String shutDown(){
        JobScraperApplication.getContext().close();
        return "shutdown request accepted";
    }
}
