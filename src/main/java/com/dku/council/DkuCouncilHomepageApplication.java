package com.dku.council;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DkuCouncilHomepageApplication {

	public static void main(String[] args) {
		SpringApplication.run(DkuCouncilHomepageApplication.class, args);
	}

}
