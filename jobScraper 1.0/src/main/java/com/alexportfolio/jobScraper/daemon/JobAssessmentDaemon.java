package com.alexportfolio.jobScraper.daemon;

import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.repository.JobRepositoryService;
import com.alexportfolio.jobScraper.service.FileService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class JobAssessmentDaemon extends Daemon{
    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionSummarizationDaemon.class);

    public JobAssessmentDaemon(JobRepositoryService jobRepositoryService, FileService fileService) {
        super(jobRepositoryService,fileService);
    }

    @Scheduled(fixedDelay=5, timeUnit=TimeUnit.MINUTES)
    public void scheduled(){
        if(llmService==null) return;
        // load cards
        loadCards();
        // evaluate
        if(records.isEmpty()) {
            logger.info("no records for making decision");
            return;
        }
        Iterator<JobCard> iterator = records.get().iterator();
        while(iterator.hasNext()){
            var card = iterator.next();
            if(card.getDescription().startsWith("summary")){
                card = makeDecision(card);
                saveCard(card);
            }
            iterator.remove();
        }
    }

    private JobCard makeDecision(JobCard card) {
        Optional<String> llmResponse = Optional.empty();

        if(card.getNotes() == null || card.getNotes().isBlank()) {
            // asking for job assessment
            llmResponse = llmService.callLLM(List.of(systemRoles.getLast(), card.getDescription(), "The resume:\n" + resume));
            if (llmResponse.isEmpty()) return card;
            // setting new notes
            card.setNotes(llmResponse.get() + "\n" + llmService.getName());
            logger.debug("Made decision on card with id " + card.getId());
        }
        return card;
    }


    @PreDestroy
    void destroy(){
        es.shutdownNow();
    }
}
