package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.BankAccounts;
import com.example.loan_platform.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountsRepository extends JpaRepository<BankAccounts, Long> {
    List<BankAccounts> findAllByUser(Users user);

    Optional<BankAccounts> findByIban(String iban);

    Optional<BankAccounts> findByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);

    boolean existsByAccountNumber(String accountNumber);
}
