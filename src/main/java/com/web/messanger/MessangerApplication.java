package com.web.messanger;

import java.time.LocalDate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.web.messanger.model.User;
import com.web.messanger.repos.UserRepository;
import com.web.messanger.service.UserService;

@SpringBootApplication
public class MessangerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext =
			SpringApplication.run(MessangerApplication.class, args);

	}
}
