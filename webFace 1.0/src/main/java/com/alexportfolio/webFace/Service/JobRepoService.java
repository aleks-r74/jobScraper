package com.alexportfolio.webFace.Service;

import com.alexportfolio.webFace.entity.JobCard;
import com.alexportfolio.webFace.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobRepoService  {
    int pageSize = 10;
    JobRepository jobRepository;

    public JobRepoService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }


    public Optional<Page<JobCard>> getPage(LocalDateTime lowerBoundDateTime, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("timeStamp").descending());
        Page<JobCard> response  = jobRepository.findAllByTimeStampGreaterThanEqual(lowerBoundDateTime, pageable);
        if(response.isEmpty()) return Optional.empty();
        return Optional.of(response);
    }
    public Optional<Page<JobCard>> getCardsWithDecision(String startsWith, LocalDateTime lowerBoundDateTime, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("timeStamp").descending());
        Page<JobCard> response  =  jobRepository.findAllByNotesStartsWithIgnoreCaseAndTimeStampGreaterThanEqual(startsWith, lowerBoundDateTime, pageable);
        if(response.isEmpty()) return Optional.empty();
        return Optional.of(response);
    }

    public Optional<Page<JobCard>> GetSummarizedCards(LocalDateTime lowerBoundDateTime, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("timeStamp").descending());
        Page<JobCard> response = jobRepository.findAllByDescriptionStartsWithAndTimeStampGreaterThanEqual("summary",lowerBoundDateTime,pageable);
        if(response.isEmpty()) return Optional.empty();
        return Optional.of(response);
    }

    public void deleteAllWithIds(List<Long> ids){
        jobRepository.deleteAllWithIds(ids);
    }

    public void setApplied(List<Long> ids){
        jobRepository.setAppliedDateTimeForIds(ids, LocalDateTime.now());
    }
}
