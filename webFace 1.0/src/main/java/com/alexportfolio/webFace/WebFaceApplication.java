package com.alexportfolio.webFace;

import com.alexportfolio.webFace.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class WebFaceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
		try {
			SpringApplication.run(WebFaceApplication.class, args);
		} catch (Exception e){
			System.out.println("Exception caught: " + e.getMessage());
		}
	}

	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
