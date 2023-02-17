package com.dku.council;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DkuCouncilHomepageApplication {

	public static void main(String[] args) {
		SpringApplication.run(DkuCouncilHomepageApplication.class, args);
	}

}
