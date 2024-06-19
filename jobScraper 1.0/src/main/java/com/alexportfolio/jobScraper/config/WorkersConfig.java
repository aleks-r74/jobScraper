package com.alexportfolio.jobScraper.config;

import com.alexportfolio.jobScraper.parser.LinkedInParser;
import com.alexportfolio.jobScraper.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Configuration
public class WorkersConfig {
    @Value("${workers.count}")
    int workersCount; // number of LinkedInParser instances
    @Value("${workers.afterLoginDelay}")
    int afterLoginDelay;
    @Bean
    @DependsOn("fileService")
    List<LinkedInParser> workers(Environment env, FileService fileService) {
        List<LinkedInParser> workers = new ArrayList<>();
        // create workers and logging in
        for (int i = 0; i < workersCount; i++) {
            try{
                var worker = new LinkedInParser(afterLoginDelay);
                worker.setFileService(fileService);
                worker.login(env.getProperty("LINKEDIN_USR"), env.getProperty("LINKEDIN_PSW"));
                workers.add(worker);
            } catch (IOException e) {
                throw new RuntimeException("Can not create a parser due to IOException in LinkedInParser");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return workers;
    }

}
