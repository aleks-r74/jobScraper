package com.alexportfolio.webFace.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

public interface MyController {

    @GetMapping("/")
    String showAbout(Model model);

    @GetMapping("/login")
    public String showLogin(Model model);

    @GetMapping("/")
    String getAll(@RequestParam(required = false) Integer page,
                  @RequestParam(required = false) LocalDateTime pageDateTime,
                  @RequestParam(required = false) String declinedByGPT,
                  Model model,
                  Principal principal);

    @PostMapping("/")
    String getAll(@RequestParam Map<String, String> params);

    @GetMapping("/settings")
    String showSettings(Model model);

    @PostMapping("/settings")
    String saveSettings(@RequestParam Map<String, String> params);
}
