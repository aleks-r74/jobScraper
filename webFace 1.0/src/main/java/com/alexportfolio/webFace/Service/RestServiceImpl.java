package com.alexportfolio.webFace.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RestServiceImpl implements RestService {
    RestTemplate restTemplate;

    @Value("${parser.url}")
    String parserUrl;

    @Value("${rest.Key}")
    String apiKey;
    HttpHeaders headers;

    public RestServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        headers = new HttpHeaders();
    }

    @PostConstruct
    public void init(){
        headers.add("Key", apiKey);

    }

    @Override
    public List<String> getLinks()  {
        String linksEndpoint = parserUrl + "/links";
        HttpEntity<List<String>> httpEntity = new HttpEntity<>(headers);
        Optional<ResponseEntity<String[]>> responseEntityOpt = Optional.empty();
        responseEntityOpt = Optional.of(restTemplate.exchange(linksEndpoint, HttpMethod.GET, httpEntity, String[].class));

        if(responseEntityOpt.isPresent() && responseEntityOpt.get().getStatusCode() == HttpStatusCode.valueOf(200))
            return Arrays.asList(responseEntityOpt.get().getBody());

        return List.of("");
    }

    @Override
    public void saveLinks(String links)  {
        String linksEndpoint = parserUrl + "/links";
        HttpEntity<String> httpEntity = new HttpEntity<>(links, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(linksEndpoint, HttpMethod.PUT, httpEntity, String.class);
    }

    @Override
    public Properties getKeywords() {
        String linksEndpoint = parserUrl + "/keywords";
        Map<String, String> response = new HashMap<>();

        HttpEntity<List<String>> httpEntity = new HttpEntity<>(headers);
        Optional<ResponseEntity<Map<String,String>>> responseEntityOpt = Optional.empty();

        ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};
        responseEntityOpt = Optional.of(restTemplate.exchange(linksEndpoint, HttpMethod.GET, httpEntity, responseType));

        if(responseEntityOpt.isPresent() && responseEntityOpt.get().getStatusCode() == HttpStatusCode.valueOf(200))
            response = responseEntityOpt.get().getBody();

        Properties prop = new Properties();
        prop.put("stopKeywords", response.getOrDefault("stopKeywords",""));
        prop.put("saveKeywords", response.getOrDefault("saveKeywords",""));

        return prop;
    }

    @Override
    public void saveKeywords(String stopKeywords, String saveKeywords)  {
        String linksEndpoint = parserUrl + "/keywords";
        Map<String, String> request = new HashMap<>();
        request.put("stopKeywords", stopKeywords);
        request.put("saveKeywords", saveKeywords);
        HttpEntity<Map<String,String>> httpEntity = new HttpEntity<>(request, headers);
        try {
            ParameterizedTypeReference<Map<String, String>> requestType = new ParameterizedTypeReference<Map<String, String>>() {};
            ResponseEntity<Map<String,String>> responseEntity = restTemplate.exchange(linksEndpoint, HttpMethod.PUT, httpEntity, requestType);
        } catch(HttpClientErrorException e){
            System.out.println("Error when PUTting keywords to " + linksEndpoint + e.getMessage());
        }
    }

    @Override
    public String getResume()  {
        String linksEndpoint = parserUrl + "/resume";
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        Optional<ResponseEntity<String>> responseEntityOpt = Optional.empty();
        responseEntityOpt = Optional.of(restTemplate.exchange(linksEndpoint, HttpMethod.GET, httpEntity, String.class));

        if(responseEntityOpt.isPresent() && responseEntityOpt.get().getStatusCode() == HttpStatusCode.valueOf(200))
            return responseEntityOpt.get().getBody();

        return "";
    }

    @Override
    public void saveResume(String resume) {
        String linksEndpoint = parserUrl + "/resume";
        HttpEntity<String> httpEntity = new HttpEntity<>(resume, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(linksEndpoint, HttpMethod.PUT, httpEntity, String.class);

    }
}
