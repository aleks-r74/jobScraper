package com.alexportfolio.jobScraper.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class FileService   {

    String path = "settings/";
    public String linksFn = "links.txt";
    public String keywordsFn = "keywords.properties";
    public String resumeFn = "resume.txt";
    public String rolesFn = "roles.txt";

    @PostConstruct
    public void init(){
        // we should wait till path is injected
        linksFn = path + linksFn;
        keywordsFn = path + keywordsFn;
        resumeFn = path + resumeFn;
        rolesFn = path + rolesFn;
    }

    // returns file content
    public List<String> readLinks() {
        List<String> list = null;
        try {
            list = Files.readAllLines(Path.of(linksFn));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return list.stream(). // removing empty lines
                map(item->item.strip())
                .collect(Collectors.toList());
    }

    // returns only valid links
    public List<String> getLinks() {
        List<String> list = null;
        try {
            list = Files.readAllLines(Path.of(linksFn));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return list.stream(). // removing empty lines
           map(item->item.strip())
            .filter(item->!item.isBlank() && item.startsWith("http"))
            .collect(Collectors.toList());
    }

    public void saveLinks(String links)  {
        try {
            Files.writeString(Path.of(linksFn), links);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getKeywords() {
        var p = new Properties();
        try(var fr = new FileReader(keywordsFn)){
            p.load(fr);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    public void saveKeywords(String stopKeywords, String saveKeywords) {
        var p = new Properties();
        p.setProperty("stopKeywords",stopKeywords);
        p.setProperty("saveKeywords",saveKeywords);
        try(var fw = new BufferedOutputStream(new FileOutputStream(keywordsFn))){
            p.store(fw,"");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResume() {
        try {
            return Files.readString(Path.of(resumeFn));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveResume(String resume) {
        try {
            Files.writeString(Path.of(resumeFn), resume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getRoles(){
        try {
            var list = Files.readAllLines(Path.of(rolesFn));
            int i=0;
            for(; i<list.size(); i++)
                if(list.get(i).startsWith("==="))
                    break;
            String role1 = list.subList(0,i).stream().collect(Collectors.joining());
            String role2 = list.subList(i+1,list.size()).stream().collect(Collectors.joining());
            return List.of(role1,role2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
