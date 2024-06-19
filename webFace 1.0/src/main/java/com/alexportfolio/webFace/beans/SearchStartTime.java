package com.alexportfolio.webFace.beans;

import com.alexportfolio.webFace.Service.JobRepoService;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;

public interface SearchStartTime {

    public LocalDateTime getDateTime();
    public void setDateTime(LocalDateTime searchStartTime);
}
