package com.uniintern.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class UniInternPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniInternPortalApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				jdbcTemplate.execute("ALTER TABLE internships ADD COLUMN IF NOT EXISTS approved_notification_seen BOOLEAN DEFAULT FALSE;");
				jdbcTemplate.execute("ALTER TABLE internships ADD COLUMN IF NOT EXISTS rejected_notification_seen BOOLEAN DEFAULT FALSE;");
				System.out.println("Database schema updated with notification columns.");
			} catch (Exception e) {
				System.out.println("Schema update check finished: " + e.getMessage());
			}
		};
	}

}
