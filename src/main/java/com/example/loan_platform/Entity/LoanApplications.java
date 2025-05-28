package com.example.loan_platform.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "loan_applications")
public class LoanApplications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "term")
    private Long term;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private LoanScoring loanScoring;


    public LoanApplications() {
    }

    public LoanApplications(Long id, Users user, Double amount, Long term, OffsetDateTime createdAt, LoanScoring loanScoring) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.term = term;
        this.createdAt = createdAt;
        this.loanScoring = loanScoring;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
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

    public LoanScoring getLoanScoring() {
        return loanScoring;
    }

    public void setLoanScoring(LoanScoring loanScoring) {
        this.loanScoring = loanScoring;
    }


}
