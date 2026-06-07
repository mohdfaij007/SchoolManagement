package com.schoolmanagement.schoolbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;
	
	@Autowired
    private TenantInterceptor tenantInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // Apply to all endpoints
				.allowedOrigins(allowedOrigins) // Allow Angular app
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
	
	
	//  Ye naya method override kiya gya hai interceptor ke liye
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                // In endpoints par tenant filter bypass ho jayega (bohot zaroori hai login ke liye)
                .excludePathPatterns("/api/auth/**", "/photos/**", "/error"); 
    }
	
}
//file:///D:/stswoksplace/SchoolManagement/uploads/student-photos/