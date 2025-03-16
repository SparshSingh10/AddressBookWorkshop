package com.example.AddressBookWorkshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableCaching

public class AddressBookWorkshopApplication {

	public static void main(String[] args) {
		// Load .env variables into system properties
		Dotenv.configure().systemProperties().load();

		// Run the Spring Boot application
		SpringApplication.run(AddressBookWorkshopApplication.class, args);
	}

}
