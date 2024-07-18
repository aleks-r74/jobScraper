package com.alexportfolio.jobScraper.daemon;



import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.repository.JobRepositoryService;
import com.alexportfolio.jobScraper.service.FileService;
import com.alexportfolio.jobScraper.service.LlmService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.*;


@Component
public class JobDescriptionSummarizationDaemon extends Daemon {

    // class variables
    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionSummarizationDaemon.class);

    public JobDescriptionSummarizationDaemon(JobRepositoryService jobRepositoryService, FileService fileService) {
        super(jobRepositoryService,fileService);
    }

    // method is called from BeanPostProcessor
    public void setLlmService(LlmService llmService) {
        this.llmService = llmService;
    }

    @Scheduled(fixedDelay=5, timeUnit=TimeUnit.MINUTES)
    public void scheduled(){
        if(llmService==null) return;
        // load cards
        loadCards();
        // evaluate
        if(records.isEmpty()) {
            logger.info("no records for summarization");
            return;
        }
        Iterator<JobCard> iterator = records.get().iterator();
        while(iterator.hasNext()){
            var card = iterator.next();
            card = summarize(card);
            saveCard(card);
            iterator.remove();
        }

    }

    private JobCard summarize(JobCard card) {
        Optional<String> llmResponse = Optional.empty();
        if(!card.getDescription().startsWith("summary")) {
            // asking for job description summarization
            llmResponse = llmService.callLLM(List.of(systemRoles.getFirst(), card.getDescription().strip()));
            if (llmResponse.isEmpty()) return card;
            // setting new job description
            card.setDescription("summary of the job description by " + llmService.getName() + "\n" + llmResponse.get() );
            logger.debug("Job description with id %d summarized".formatted(card.getId()));
        }
        return card;
    }


    @PreDestroy
    void destroy(){
        es.shutdownNow();
    }


}
