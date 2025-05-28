package com.example.loan_platform.DTO.Entity;

import com.example.loan_platform.Entity.Enum.CurrencyAccounts;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;

public class BankAccountsDto {
    private Long id;
    @JsonIgnore
    private UsersDto user;
    private String bankName;
    private String accountNumber;
    private String iban;
    private CurrencyAccounts currency;
    private OffsetDateTime createdAt;

    public BankAccountsDto() {
    }

    public BankAccountsDto(Long id, UsersDto user, String bankName, String accountNumber, String iban, CurrencyAccounts currency, OffsetDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.iban = iban;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersDto getUser() {
        return user;
    }

    public void setUser(UsersDto user) {
        this.user = user;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public CurrencyAccounts getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyAccounts currency) {
        this.currency = currency;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



