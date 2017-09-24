package org.hssh.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReliableMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReliableMqApplication.class, args);
	}
}
