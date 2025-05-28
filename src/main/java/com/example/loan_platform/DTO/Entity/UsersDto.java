package com.example.loan_platform.DTO.Entity;

import com.example.loan_platform.Entity.Enum.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.OffsetDateTime;


public class UsersDto {
    private Long id;
    private String name;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING)// Enum'ı JSON'da daima string olarak göstermek için kullanılır.
    private UserRole userRole;
    private String passwordHash;
    private Double income;
    private Double creditScore;
    private OffsetDateTime createdAt;

    public UsersDto() {
    }

    public UsersDto(Long id) {
        this.id = id;
    }

    public UsersDto(Long id, String name, String email, UserRole userRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userRole = userRole;
    }

    public UsersDto(Long id, String name, String email, UserRole userRole, String passwordHash, Double income) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userRole = userRole;
        this.passwordHash = passwordHash;
        this.income = income;
    }

    public UsersDto(Long id, String name, String email, UserRole userRole, String passwordHash, Double income, Double creditScore, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userRole = userRole;
        this.passwordHash = passwordHash;
        this.income = income;
        this.creditScore = creditScore;
        this.createdAt = createdAt;
    }


    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
