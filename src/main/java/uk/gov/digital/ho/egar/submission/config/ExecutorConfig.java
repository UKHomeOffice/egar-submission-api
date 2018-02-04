package uk.gov.digital.ho.egar.submission.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!sync")
@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(4);
    }

}