package com.alexportfolio.jobScraper;

import com.alexportfolio.jobScraper.beans.TaskRunner;
import com.alexportfolio.jobScraper.service.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EntityScan("com.alexportfolio.jobScraper.entity")
public class JobScraperApplication {
	@Value("${parsing.delay}")
	int parsingDelay;
	@Value("${cleanup.delay}")
	int cleanupDelay;
	static ConfigurableApplicationContext context;
	private static final Logger logger = LoggerFactory.getLogger(JobScraperApplication.class);

	public static void main(String[] args) {
		context = SpringApplication.run(JobScraperApplication.class, args);
	}


	@Bean
	CommandLineRunner commandLineRunner(TaskRunner taskRunner, ParserService parserService){
		return r->{
			Runnable parsingTask = ()->{
				logger.info("Parsing task started on " + currentTime());
				parserService.runParsing();
				logger.info("Parsig task finished on %s\nDelay is %d min\n".formatted(currentTime(),parsingDelay));

			};
			Runnable cleaningTask = parserService::removeOldCardsFromDB;
			taskRunner.start(parsingTask, parsingDelay);
			taskRunner.start(cleaningTask, cleanupDelay);
		};

	}
	String currentTime(){
		var formatter = DateTimeFormatter.ofPattern("MMMM/dd/yyyy 'at' hh:mm ");
		return LocalDateTime.now().format(formatter);
	}

	public static ConfigurableApplicationContext getContext() {
		return context;
	}
}
