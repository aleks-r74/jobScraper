package com.alexportfolio.jobScraper.service;


import com.alexportfolio.jobScraper.JobScraperApplication;
import com.alexportfolio.jobScraper.entity.JobCard;
import com.alexportfolio.jobScraper.parser.LinkedInParser;
import com.alexportfolio.jobScraper.repository.JobRepositoryService;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


@Service
public class ParserServiceImpl implements ParserService {
    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);
    public Set<JobCard> storage;
    public Set<JobCard> storageProjection; // contains item from DB - only title & company name
    FileService fileService;
    Environment env;
    JobRepositoryService jobRepositoryService;
    List<LinkedInParser> workers;
    final int expirationThreshold = 30;

    @Autowired
    public ParserServiceImpl(FileService fileService, List<LinkedInParser> workers, JobRepositoryService repo) {
        this.fileService = fileService;
        this.env = env;
        storage =  new HashSet<JobCard>();
        storageProjection =  new HashSet<JobCard>();
        this.workers = workers;
        this.jobRepositoryService = repo;
    }
    @PreDestroy
    void destroyWorkers(){
        workers.forEach(LinkedInParser::destroy);
    }
    void runJobTitleParsing() {
        logger.debug("run JobTitleParsing()");
        // split links into chunks, number of chunks <= number of workers
        List<List<String>> splitLinksList= splitList(fileService.getLinks(), workers.size());
        logger.debug("splitLinksList.size() is "+splitLinksList.size());
        // virtual threads
        ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
        // future results
        List<Future<Set<JobCard>>> resultList = new ArrayList<>();
        // feed each chunk to a pool of workers
        for(int w=0; w < workers.size(); w++){
                var worker = workers.get(w); // each worker
                if(splitLinksList.size()-1 < w) continue; // if there is a chunk for this worker
                var chunkLinksList = splitLinksList.get(w); // gets its own chunk of links
                Callable<Set<JobCard>> parsingTask = () -> worker.parseJobCards(chunkLinksList);
                // shoot the parsing task to a virtual thread and store future results in a list
                resultList.add(es.submit(parsingTask));
                logger.debug("task submitted for worker " + w);
        }
        // wait until all cards parsed and add them to the storage
        storage.clear();
        for(var result: resultList){
            try {
                storage.addAll(result.get());
            }
            catch (ExecutionException | InterruptedException e) {
                logger.debug("Exception while runJobTitleParsing's parsingTask " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        logger.debug("Job titles are parsed");
        es.shutdown();
    }
    void runJobDescriptionParsing(){
        // split storage into chunks
        List<List<JobCard>> splitCardsList;
        splitCardsList = splitList(List.copyOf(storage), workers.size());

        // virtual threads
        ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
        // future results
        List<Future<List<JobCard>>> resultList = new ArrayList<>();
        // feed each chunk of links to worker
        for(int i=0; i<splitCardsList.size();i++){
            var worker = workers.get(i);                // this worker
            var chunkCardsList = splitCardsList.get(i); // cards for this worker
            Callable<List<JobCard>> parsingTask = () -> worker.fillDescription(chunkCardsList);
            // shoot the parsing task to a virtual thread and store future results in a list
            resultList.add(es.submit(parsingTask));
        }
        // wait until all cards parsed and add them to the storage
        HashSet<JobCard> filledCards = new HashSet<>();
        for(var result: resultList){
            try {
                filledCards.addAll(Set.copyOf(result.get()));
            } catch (ExecutionException | InterruptedException e) {
                logger.debug("Exception while runJobDescriptionParsing's parsingTask " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        storage = filledCards;
        es.shutdown();
    }
    
    // splits any list into number of chunks
    public <T> List<List<T>> splitList(List<T> list, int chunks){
        if(chunks>list.size()) return new ArrayList<>(List.of(list));
        var chunkSize = (int) Math.ceil((double) list.size() / chunks);
        List<List<T>> splittedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            splittedList.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return splittedList;
    }
    void loadStorageProjection(){
        // loading only company Name and Job Title form DB
        var dbResponse = jobRepositoryService.findAllOnlyTitleAndCompanyName();
        if(dbResponse.isPresent())
            storageProjection.addAll(dbResponse.get());
     }

    @Override
    public void removeOldCardsFromDB() {
        // remove old entries from DB
        LocalDateTime threshold = LocalDateTime.now().minusDays(expirationThreshold);
        jobRepositoryService.deleteAllWhereTime_stampLowerThan(threshold);
    }

    @Override
    public void runParsing(){
        try {
            // load title & company name from DB
            loadStorageProjection();
            logger.info("Projections loaded - " + storageProjection.size());
            // gathering job cards to storage
            logger.info("Start parsing titles");
            runJobTitleParsing();
            // remove from storage those cards that present in storageProjection
            storage.removeAll(storageProjection);
            logger.info("New cards found: " + storage.size());
            // parse description for remaining in storage
            runJobDescriptionParsing();
            // persist parsed cards to DB
            jobRepositoryService.saveAll(storage);
        }
        catch (RuntimeException e){
            JobScraperApplication.restart();
        }
    }
}
