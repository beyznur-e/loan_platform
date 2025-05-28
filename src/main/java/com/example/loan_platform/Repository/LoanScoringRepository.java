package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanScoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanScoringRepository extends JpaRepository<LoanScoring, Long> {
    @Query("SELECT ls.application FROM LoanScoring ls WHERE ls.decision = 'APPROVED'")
    List<LoanApplications> findApprovedApplications();
}
