package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.Documents;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<Documents, Long> {
    List<Documents> findByUser(Users user);

    List<Documents> findByApplication(LoanApplications application);
}
