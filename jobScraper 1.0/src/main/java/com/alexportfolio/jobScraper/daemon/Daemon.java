package com.alexportfolio.jobScraper.daemon;

import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.repository.JobRepositoryService;
import com.alexportfolio.jobScraper.service.FileService;
import com.alexportfolio.jobScraper.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

abstract class Daemon {
    private static final Logger logger = LoggerFactory.getLogger(Daemon.class);
    // beans
    JobRepositoryService jobRepositoryService;
    LlmService llmService;
    FileService fileService;
    ScheduledExecutorService es;

    static List<String> systemRoles;
    static String resume;
    public Optional<Set<JobCard>> records = Optional.empty();

    public Daemon(JobRepositoryService jobRepositoryService, FileService fileService) {
        this.jobRepositoryService = jobRepositoryService;
        this.fileService = fileService;
        es = Executors.newSingleThreadScheduledExecutor();
        systemRoles = fileService.getRoles();
        resume = fileService.getResume();
    }

    // load Records from DB (only for last 24hrs)
    public void loadCards(){
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            var timeFilter = LocalDateTime.now().minusDays(1);
            var results = jobRepositoryService.findAllByNotesIsNullAndTimeStampGreaterThanEqual(timeFilter, pageable);
            if(results.isEmpty()) {
                records = Optional.empty();
                return;
            }
            var cards = Collections.synchronizedSet(results.get().stream().collect(Collectors.toSet()));
            records = Optional.of(cards);
    }

    public void saveCard(JobCard card){
        if(card == null) return;
        jobRepositoryService.updateNoteAndDescriptionForId(card.getId(), card.getDescription(), card.getNotes());
    }

    public void setLlmService(LlmService llmService) {
        this.llmService = llmService;
    }

}
