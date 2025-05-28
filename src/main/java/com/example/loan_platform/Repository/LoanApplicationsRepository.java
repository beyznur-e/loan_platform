package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationsRepository extends JpaRepository<LoanApplications, Long> {
    List<LoanApplications> findByUser(Users user);
}
