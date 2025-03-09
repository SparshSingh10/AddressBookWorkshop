package com.example.AddressBookWorkshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AddressBookWorkshopApplication {

	public static void main(String[] args) {
		// Load .env variables into system properties
		Dotenv.configure().systemProperties().load();

		// Run the Spring Boot application
		SpringApplication.run(AddressBookWorkshopApplication.class, args);
	}
}
