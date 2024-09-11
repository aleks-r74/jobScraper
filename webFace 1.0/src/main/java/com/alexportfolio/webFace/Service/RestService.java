package com.alexportfolio.webFace.service;

import java.util.List;
import java.util.Properties;

public interface RestService {

    List<String> getLinks();

    void saveLinks(String links);

    Properties getKeywords();

    void saveKeywords(String stopKeywords, String saveKeywords);

    String getResume();

    void saveResume(String resume);
}
