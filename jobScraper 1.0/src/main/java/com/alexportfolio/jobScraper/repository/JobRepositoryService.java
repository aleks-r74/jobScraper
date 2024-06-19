package com.alexportfolio.jobScraper.repository;


import com.alexportfolio.jobScraper.entity.JobCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
public class JobRepositoryService {
    JobRepository jobRepository;
    private static final Logger logger = LoggerFactory.getLogger(JobRepositoryService.class);

    public JobRepositoryService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Optional<Set<JobCard>> findAllOnlyTitleAndCompanyName(){
        Set<JobCard> result = null;
        try {
            result = jobRepository.findAllOnlyTitleAndCompanyName();
        }catch(RuntimeException e){
            logger.debug("Exception durin db access");
            return Optional.empty();
            }
        if(result!=null && result.isEmpty())
            return Optional.empty();
        return Optional.of(result);
    }

    public void deleteAllWhereTime_stampLowerThan(LocalDateTime timeStamp){
       try{
           jobRepository.deleteAllWhereTime_stampLowerThan(timeStamp);
       }catch(RuntimeException e){
           logger.debug("Exception durin db access");
       }
    }

    public void updateNoteAndDescriptionForId(@Param("id") Long id, @Param("description") String description, @Param("note") String note){
        try{
            jobRepository.updateNoteAndDescriptionForId(id,description,note);
        }catch(RuntimeException e){
            logger.debug("Exception durin db access");
        }
    }

    public Optional<Page<JobCard>> findAllByNotesIsNullAndTimeStampGreaterThanEqual(@Param("lowerBound") LocalDateTime lowerBound, Pageable pageable){
        Page<JobCard> result = null;
        try{
            result = jobRepository.findAllByNotesIsNullAndTimeStampGreaterThanEqual(lowerBound,pageable);
        }catch(RuntimeException e){
            logger.debug("Exception durin db access");
            return Optional.empty();
        }
        if(result!=null && result.isEmpty())
            return Optional.empty();
        return Optional.of(result);
    }

    public void saveAll(Iterable<JobCard> set){
        try {
            jobRepository.saveAll(set);
        } catch(RuntimeException e){
            logger.debug("Exception durin db access");
        }
    }
}
