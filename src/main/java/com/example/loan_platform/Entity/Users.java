package com.example.loan_platform.Entity;

import com.example.loan_platform.Entity.Enum.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique=true) //JUnit testte hata vermedi.
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @Column(name = "income")
    private Double income;  // Kullanıcının geliri

    @Column(name = "credit_score")
    private Double creditScore;  // Kullanıcının kredi skoru

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<LoanApplications> loanApplications = new HashSet<>();


    public Users() {
    }

    public Users(Long id, String name, String email, String passwordHash, UserRole userRole, OffsetDateTime createdAt, Double income, Double creditScore) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.income = income;
        this.creditScore = creditScore;
    }

    public Users(Long id, String name, String email, String passwordHash, UserRole userRole, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }


    public Users(String name, String email, String passwordHash, UserRole userRole, OffsetDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Double creditScore) {
        this.creditScore = creditScore;
    }

    public Set<LoanApplications> getLoanApplications() {
        return loanApplications;
    }

    public void setLoanApplications(Set<LoanApplications> loanApplications) {
        this.loanApplications = loanApplications;
    }
}
