package com.alexportfolio.webFace.beans;

import com.alexportfolio.webFace.service.JobRepoService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;

/*
 This bean is used to fill date-time picker on the result page
 */
@Component
@SessionScope
public class SearchStartTimeImpl implements SearchStartTime {
    LocalDateTime searchStartTime;

    public SearchStartTimeImpl(JobRepoService jobRepoService) {
        searchStartTime = LocalDateTime.now().minusDays(1);
    }

    public LocalDateTime getDateTime() {
        return searchStartTime;
    }

    public void setDateTime(LocalDateTime searchStartTime) {
        this.searchStartTime = searchStartTime;
    }
}
