package com.web.messanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MessangerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext =
			SpringApplication.run(MessangerApplication.class, args);
	}
}
