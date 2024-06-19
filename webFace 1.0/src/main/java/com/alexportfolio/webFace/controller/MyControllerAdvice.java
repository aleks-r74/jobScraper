package com.alexportfolio.webFace.controller;

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
}
