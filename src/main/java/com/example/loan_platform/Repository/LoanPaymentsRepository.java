package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.Enum.StatusPayments;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanPayments;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanPaymentsRepository extends JpaRepository<LoanPayments, Long> {
    List<LoanPayments> findByApplication(LoanApplications application);

    List<LoanPayments> findByStatus(StatusPayments status);

}
