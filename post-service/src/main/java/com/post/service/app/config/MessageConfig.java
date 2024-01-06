package com.post.service.app.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(
                "classpath:/messages/apirest_error_messages",
                "classpath:/messages/apirest_response_messages"
        );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
