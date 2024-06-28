package com.alexportfolio.webFace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.ConnectException;

@ControllerAdvice
public class MyControllerAdvice {
    @ExceptionHandler(ConnectException.class)
    @ResponseBody
    public String handle(ConnectException ex){
        return "No service: " + ex.getMessage();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        StringBuilder supportedMethods = new StringBuilder();
        ex.getSupportedHttpMethods().forEach(method -> supportedMethods.append(method + " "));
        return new ResponseEntity<>(
                "HTTP method not supported. Supported methods are: " + supportedMethods.toString().trim(),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

}
