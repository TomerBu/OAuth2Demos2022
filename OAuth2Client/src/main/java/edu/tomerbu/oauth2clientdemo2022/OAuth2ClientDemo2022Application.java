package edu.tomerbu.oauth2clientdemo2022;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OAuth2ClientDemo2022Application {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ClientDemo2022Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
