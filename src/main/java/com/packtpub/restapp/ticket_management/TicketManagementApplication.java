package com.packtpub.restapp.ticket_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "com.packtpub")
@EnableJpaRepositories("com.packtpub.model.repository")
@EntityScan("com.packtpub.model")
public class TicketManagementApplication{

	public static void main(String[] args) {
		SpringApplication.run(TicketManagementApplication.class, args);
	}

}
