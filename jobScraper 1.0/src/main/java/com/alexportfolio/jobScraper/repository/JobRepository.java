package com.alexportfolio.jobScraper.repository;

import com.alexportfolio.jobScraper.entity.JobCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public interface JobRepository extends PagingAndSortingRepository<JobCard, Long>, CrudRepository<JobCard, Long> {

    @Query("SELECT new JobCard(e.title, e.company) FROM JobCard e")
    Set<JobCard> findAllOnlyTitleAndCompanyName();

    @Modifying
    @Transactional
    @Query("DELETE FROM JobCard j WHERE j.timeStamp < :timeStamp")
    void deleteAllWhereTime_stampLowerThan(LocalDateTime timeStamp);


    @Modifying
    @Transactional
    @Query("UPDATE JobCard j SET j.notes = :note, j.description = :description WHERE j.id = :id")
    int updateNoteAndDescriptionForId(@Param("id") Long id, @Param("description") String description, @Param("note") String note);


    // returns all cards where Notes field is null
    Page<JobCard> findAllByNotesIsNullAndTimeStampGreaterThanEqual(@Param("lowerBound") LocalDateTime lowerBound, Pageable pageable);
}
