package com.alexportfolio.jobScraper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LlmServiceOpenAi implements LlmService{

    OpenAiApi openAiApi;

    @Value("${llmServiceOpenAi.model}")
    String model = "gpt-3.5-turbo-0125";
    @Value("${llmServiceOpenAi.tokenLimit}")
    Integer tokenLimit = 60_000;
    Integer limitTimeFrame = 60_000;
    @Value("${llmServiceOpenAi.requestsPerMinLimit}")
    Integer rpmLimit = 500;
    @Value("${llmServiceOpenAi.tokenPerDayLimit}")
    Integer tokenPerDayLimit = 200_000;

    private static final Logger logger = LoggerFactory.getLogger(LlmServiceOpenAi.class);

    static class RequestInfo{
        static long firstRequestThisDayTime;
        static boolean firstRequest = true;
        static long lastRequestTime;
        static int tokensConsumed;
        static int tokensConsumedToday;
        static int requests;
        static void reset(){
            tokensConsumed = 0;
            requests = 0;
        }
        static void resetAll(){
            tokensConsumed = 0;
            tokensConsumedToday = 0;
            requests = 0;
            firstRequest=true;
        }
    }

    public LlmServiceOpenAi() {
        openAiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));
    }

    @Override
    public Optional<String> callLLM(List<String> prompt) {
        List<ChatCompletionMessage> messages = new ArrayList<>();
        if(prompt == null || prompt.size()<2) throw new RuntimeException("prompt should have at least 2 messages");
        // first message is system role
        messages.add(new OpenAiApi.ChatCompletionMessage(prompt.getFirst(), ChatCompletionMessage.Role.SYSTEM));
        // starting from second message - user messages
        for (int i = 1; i < prompt.size(); i++) {
            messages.add(new OpenAiApi.ChatCompletionMessage(prompt.get(i), ChatCompletionMessage.Role.USER));
        }

        // checking rate limit
        if(System.currentTimeMillis() < (RequestInfo.firstRequestThisDayTime + 60*60*24*1000)){
            if (RequestInfo.tokensConsumedToday>=tokenPerDayLimit) {
                logger.info("Daily rate limit exceeded");
                return Optional.empty();
            }
        } else{
            RequestInfo.resetAll();
        }

        if((System.currentTimeMillis() < RequestInfo.lastRequestTime+limitTimeFrame) &&
                (RequestInfo.tokensConsumed >= tokenLimit || RequestInfo.requests >= rpmLimit))
        {
            // wait
            try {
                long timeToWait = limitTimeFrame-(System.currentTimeMillis()-RequestInfo.lastRequestTime);
                logger.info("rate limit exceeded, waiting: " + timeToWait);
                Thread.sleep(timeToWait);
                // clear limits
                RequestInfo.reset();
            } catch (InterruptedException e) {
                throw new RuntimeException("thread was interrupted while sleeping");
            }
        }

        // perform call
        var timeBefore = System.currentTimeMillis();
        ResponseEntity<OpenAiApi.ChatCompletion> response = openAiApi.chatCompletionEntity(
                                                                new OpenAiApi.ChatCompletionRequest(messages, model, 0.0f));
        RequestInfo.requests++;
        var duration = (System.currentTimeMillis()-timeBefore) / 1000;
        logger.debug("The request to OpenAI took %d sec".formatted(duration));

        var responseObj = response.getBody();
        String content = ""; // return empty string by default
        int tokens = 0;
        if(responseObj!=null){
            content = responseObj.choices().getFirst().message().content().replaceAll("\n+", "\n");;
            RequestInfo.tokensConsumed += responseObj.usage().totalTokens();
            RequestInfo.tokensConsumedToday += responseObj.usage().totalTokens();
        }
        RequestInfo.lastRequestTime = System.currentTimeMillis();
        if(RequestInfo.firstRequest){
            RequestInfo.firstRequestThisDayTime = RequestInfo.lastRequestTime;
            RequestInfo.firstRequest = false;
        }
        return Optional.of(content);
    }

    @Override
    public String getName() {
        return "OpenAI";
    }
}
