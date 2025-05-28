package com.example.loan_platform.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    @Email
    private String email;

    @Size(min = 8)
    private String passwordHash;


    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
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

}


