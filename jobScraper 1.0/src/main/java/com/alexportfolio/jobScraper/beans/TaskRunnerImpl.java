package com.alexportfolio.jobScraper.beans;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TaskRunnerImpl implements TaskRunner {
    ScheduledExecutorService es;

    public TaskRunnerImpl(){
        es = Executors.newSingleThreadScheduledExecutor();
    }
	@Override
    public void start(Runnable task, int delayMin){
        es.scheduleWithFixedDelay(task, 0,delayMin, TimeUnit.MINUTES);
    }
    @PreDestroy
    void preDestroy(){
        es.shutdown();
    }
}
