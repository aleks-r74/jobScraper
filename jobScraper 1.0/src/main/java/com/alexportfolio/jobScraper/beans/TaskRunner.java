package com.alexportfolio.jobScraper.beans;

public interface TaskRunner {
    void start(Runnable task, int delayMin);
}
