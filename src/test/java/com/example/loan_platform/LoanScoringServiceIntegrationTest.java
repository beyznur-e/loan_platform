package com.example.loan_platform;


import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanScoring;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.LoanScoringRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.LoanScoringService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class LoanScoringServiceIntegrationTest {
    @Autowired
    private LoanScoringRepository loanScoringRepository;

    @Autowired
    private LoanApplicationsRepository loanApplicationsRepository;

    @Autowired
    private LoanScoringService loanScoringService;

    @Autowired
    private UsersRepository usersRepository;

    private LoanApplications testApplication;

    @BeforeEach
    void setUp() {
        Users testUser = new Users();
        testUser.setName("Test User");
        testUser.setEmail("test3@example.com");
        testUser.setPasswordHash("password");
        testUser.setUserRole(UserRole.CUSTOMER);
        testUser.setIncome(500.0);
        testUser.setCreditScore(100.0);

        usersRepository.save(testUser);

        testApplication = new LoanApplications();
        testApplication.setUser(testUser);
        testApplication.setAmount(10000.0); // ✅ amount değerini burada doğru şekilde set edin.
        testApplication.setTerm(12L); // ✅ NOT NULL alan
        //testApplication.setCreatedAt(OffsetDateTime.now()); // ✅ NOT NULL alan

        // Veritabanına kaydet
        loanApplicationsRepository.save(testApplication);
    }


    @Test
    void testEvaluateLoanApplications_Approved() {

        loanScoringService.evaluateLoanApplications(testApplication.getId());

        LoanScoring result = loanScoringRepository.findAll().get(0);

        assertNotNull(result);
        assertEquals(Decision.APPROVED, result.getDecision());
        assertTrue(result.getCalculatedScore() > 50);
    }


    @Test
    void testEvaluateLoanApplications_Rejected() {


        loanScoringService.evaluateLoanApplications(testApplication.getId());

        List<LoanScoring> results = loanScoringRepository.findAll();

        if (!results.isEmpty()) {
            LoanScoring result = results.get(0); // İlk öğe güvenli bir şekilde alınır
            assertNotNull(result);
            assertEquals(Decision.REJECTED, result.getDecision());
            assertTrue(result.getCalculatedScore() < 50);
        }

    }
}
