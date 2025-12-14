package com.schoolmanagement;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
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
