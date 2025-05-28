package com.example.loan_platform;

import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.LoanScoring;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BatchTestConfig {


    @Autowired
    private JobLauncher jobLauncher; // Otomatik enjekte edilecek

    @Autowired
    private Job loanPaymentJob; // BatchConfig'te tanımlı Job

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        JobLauncherTestUtils utils = new JobLauncherTestUtils();
        utils.setJobLauncher(jobLauncher);
        utils.setJob(loanPaymentJob);
        return utils;


    }
}