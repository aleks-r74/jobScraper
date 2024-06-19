package com.alexportfolio.jobScraper.controller;

import com.alexportfolio.jobScraper.daemon.JobDescriptionSummarizationDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RestAdvice {
    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionSummarizationDaemon.class);

    @ExceptionHandler(RuntimeException.class)
    void RuntimeExceptionHandler(RuntimeException e){
        logger.info("Exception in Rest: " + e.getMessage() + e.getCause());
    }

    @ExceptionHandler(Exception.class)
    void ExceptionHandler(Exception e){
        logger.info("Exception in Rest: " + e.getMessage() + e.getCause());
    }
}
