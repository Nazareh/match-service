package com.turminaz.matchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

@SpringBootApplication
@EnableMongoRepositories
public class MatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchServiceApplication.class, args);
	}


}
