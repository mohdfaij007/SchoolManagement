package com.schoolmanagement;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class SchoolManagementApplication {

	public static void main(String[] args) {
		
		
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		
		SpringApplication.run(SchoolManagementApplication.class, args);
	
	
	
	} 
	
//	@PostConstruct
//    public void init() {
//        // FORCE the timezone to the modern name expected by PostgreSQL
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
//        System.out.println("✅ TimeZone forced to: " + TimeZone.getDefault().getID());
//    }

}
