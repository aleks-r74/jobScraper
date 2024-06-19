package com.alexportfolio.jobScraper.service;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@Primary
public class LlmServiceOllama implements LlmService{

    private static final Logger logger = LoggerFactory.getLogger(LlmServiceOpenAi.class);
    OllamaApi ollamaApi;
    @Value("${ollama.model}")
    String model = "";
    @Value("${ollama.url}")
    String baseUrl;

    public LlmServiceOllama() {
        this.ollamaApi = new OllamaApi();
    }
    @PostConstruct
    private void init(){
        if(baseUrl!=null && !baseUrl.isBlank())
            this.ollamaApi = new OllamaApi(baseUrl);
    }

    @Override
    public Optional<String> callLLM(List<String> prompt) {
        if(prompt == null || prompt.size()<2) throw new RuntimeException("prompt should have at least 2 messages");
        if(model.isEmpty()) throw new RuntimeException("ollama.model is not defined");

        List<Message> messages = new ArrayList<>();

        // creating messages, first message is system role
        messages.add(Message.builder(Message.Role.SYSTEM)
                .withContent(prompt.getFirst())
                .build());
        for (int i = 1; i < prompt.size(); i++) {
            messages.add(Message.builder(Message.Role.USER)
                    .withContent(prompt.get(i))
                    .build());
        }
        // creating request
        var request = OllamaApi.ChatRequest.builder(model)
                .withStream(false) // not streaming
                .withMessages(messages)
                .withOptions(OllamaOptions.create().withTemperature(0.0f).withLowVRAM(false).withNumThread(4))
                .build();

        var timeBefore = System.currentTimeMillis();
        String response = ollamaApi.chat(request).message().content().replaceAll("\n+", "\n");
        var duration = (System.currentTimeMillis()-timeBefore) / 1000;
        logger.debug("The request to %s took %d sec ".formatted(model, duration));
        return Optional.of(response);
    }

    @Override
    public String getName() {
        return model;
    }


}
