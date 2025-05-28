package com.example.loan_platform.DTO.Entity;

import java.time.OffsetDateTime;

public class LoanApplicationsDto {
    private Long id;
    private UsersDto user;
    private Double amount;
    private Long term;
    private OffsetDateTime createdAt;

    public LoanApplicationsDto() {
    }

    public LoanApplicationsDto(Long id, UsersDto user, Double amount, Long term) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.term = term;
    }

    public LoanApplicationsDto(Long id, UsersDto user, Double amount, Long term, OffsetDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.term = term;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
