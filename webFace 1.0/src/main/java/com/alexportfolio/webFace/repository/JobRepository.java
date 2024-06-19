package com.alexportfolio.webFace.repository;

import com.alexportfolio.webFace.entity.JobCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends PagingAndSortingRepository<JobCard,Long> {
    Page<JobCard> findAllByTimeStampGreaterThanEqual(@Param("lowerBound") LocalDateTime lowerBound, Pageable pageable);

    Page<JobCard> findAllByDescriptionStartsWithAndTimeStampGreaterThanEqual(@Param("startsWith") String startsWith,
                                                                             @Param("lowerBound") LocalDateTime lowerBound,
                                                                             Pageable pageable);

    Page<JobCard> findAllByNotesStartsWithIgnoreCaseAndTimeStampGreaterThanEqual(
            @Param("startsWith") String startsWith,
            @Param("lowerBound") LocalDateTime lowerBound,
            Pageable pageable);



    @Modifying
    @Transactional
    @Query("DELETE FROM JobCard j WHERE j.timeStamp <= :timeStamp")
    void deleteAllWhereTime_stampLowerThan(LocalDateTime timeStamp);
    @Modifying
    @Transactional
    @Query("DELETE FROM JobCard WHERE id IN :ids")
    void deleteAllWithIds(List<Long> ids);
    @Modifying
    @Transactional
    @Query("UPDATE JobCard e SET e.applied = :dateTime WHERE e.id IN :ids")
    void setAppliedDateTimeForIds(List<Long> ids, LocalDateTime dateTime);
}
