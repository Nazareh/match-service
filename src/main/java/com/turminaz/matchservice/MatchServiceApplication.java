package com.turminaz.matchservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MatchServiceApplication {

	/*
	 * You need this main method (empty) or explicit <start-class>example.MatchServiceApplication</start-class>
	 * in the POM to ensure boot plug-in makes the correct entry
	 */
	public static void main(String[] args) {
		// empty unless using Custom runtime at which point it should include
		// SpringApplication.run(MatchServiceApplication.class, args);
	}


}
