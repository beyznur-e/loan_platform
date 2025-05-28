package com.example.loan_platform;

import com.example.loan_platform.Config.BatchConfig;
import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.Enum.StatusPayments;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanPayments;
import com.example.loan_platform.Entity.LoanScoring;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.LoanPaymentsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        LoanPlatformApplication.class,
        BatchConfig.class,
        BatchTestConfig.class
})
@Transactional
@Import(BatchTestConfig.class)
@ActiveProfiles("test")
public class LoanPaymentBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private LoanApplicationsRepository applicationsRepository;

    @Autowired
    private LoanPaymentsRepository paymentsRepository;

    @Autowired
    private UsersRepository usersRepository;


    @Test
    public void testBatchJob() throws Exception {
        Users user = new Users();
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPasswordHash("hashedpassword123");
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.CUSTOMER);
        user.setIncome(500.0);
        user.setCreditScore(100.0);
        usersRepository.save(user);


        LoanApplications application = new LoanApplications();
        application.setLoanScoring(new LoanScoring());
        application.setAmount(1000.0);
        application.setTerm(5L);
        application.setCreatedAt(LocalDateTime.now().atOffset(ZoneOffset.UTC)); // UTC offset ile
        application.setUser(user);
        applicationsRepository.save(application);


        LoanScoring scoring = new LoanScoring();
        scoring.setDecision(Decision.APPROVED); // ← Bu kritik!
        scoring.setApplication(application);
        application.setLoanScoring(scoring);


        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        List<LoanPayments> payments = paymentsRepository.findAll();
        assertEquals(5, payments.size()); // 5 taksit oluşmalı
        assertEquals(200.0, payments.get(0).getAmount()); // 1000/5 = 200
        assertAll(
                () -> assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus()),
                () -> assertEquals(5, payments.size()),
                () -> assertTrue(payments.stream().allMatch(p -> p.getStatus() == StatusPayments.PENDING))
        );

    }

    @AfterEach
    public void cleanup() {
        paymentsRepository.deleteAll();
        applicationsRepository.deleteAll();
        usersRepository.deleteAll();
    }
}