package com.example.loan_platform;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EntityScan("com.example.loan_platform.Entity")
@EnableJpaRepositories("com.example.loan_platform.Repository")
@EnableBatchProcessing
public class LoanPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanPlatformApplication.class, args);
    }

}
