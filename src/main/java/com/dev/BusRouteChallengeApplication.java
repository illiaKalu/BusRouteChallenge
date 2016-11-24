package com.dev;

import com.dev.Configs.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BusRouteChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run( new Class[] { BusRouteChallengeApplication.class,
				ApplicationConfig.class }, args);
	}
}
