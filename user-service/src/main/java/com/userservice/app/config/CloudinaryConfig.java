package com.userservice.app.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Autowired
    private Environment environment;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", environment.getProperty("cloudinary.cloud_name"));
        config.put("api_key", environment.getProperty("cloudinary.api_key"));
        config.put("api_secret", environment.getProperty("cloudinary.api_secret"));
        return new Cloudinary(config);
    }
}
