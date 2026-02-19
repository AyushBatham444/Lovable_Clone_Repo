package com.project.demo.config;


// This is Cross Origin Resource Sharing config ie it allows this backend's function to be used on the frontend (ie on 5173 right now (on vs code))

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry)
            {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
                        .allowedHeaders("*") // Your backend will accept ANY HTTP header sent by the frontend.
                        .allowCredentials(true); // this will allow cockies , authorization errors etc as well
            }
        };
    }
}
