package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.AuditLogs;
import com.example.loan_platform.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetTime;
import java.util.List;

@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLogs, Long> {
    List<AuditLogs> findByUser(Users user);

    List<AuditLogs> findByAccountNumber(String accountNumber);
}
