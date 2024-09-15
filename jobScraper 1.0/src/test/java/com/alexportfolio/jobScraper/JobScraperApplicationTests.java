package com.alexportfolio.jobScraper;

import com.alexportfolio.jobScraper.parser.LinkedInParser;
import com.alexportfolio.jobScraper.service.ParserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;

import java.util.List;

//@SpringBootTest
class JobScraperApplicationTests {
	@MockBean
	List<LinkedInParser> workers;
	@MockBean
	ParserService parserService;


	//@Test
	@Disabled
	void contextLoads() {
	}

}
