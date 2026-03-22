package com.schoolmanagement.schoolbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // Apply to all endpoints
				.allowedOrigins("http://localhost:4200",
						"https://school-management-ui-psi.vercel.app/") // Allow Angular app
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
				.allowedHeaders("*") // Allow all headers
				.allowCredentials(true);
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL "/photos/**" to the file system folder "uploads/student-photos/"
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("file:uploads/student-photos/");
    }
	
}
//file:///D:/stswoksplace/SchoolManagement/uploads/student-photos/