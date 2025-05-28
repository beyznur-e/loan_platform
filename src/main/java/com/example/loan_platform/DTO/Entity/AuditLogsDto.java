package com.example.loan_platform.DTO.Entity;

import java.time.OffsetDateTime;

public class AuditLogsDto {

    private Long id;
    private UsersDto usersDto;
    private String action;
    private String accountNumber;
    private OffsetDateTime createdAt;

    public AuditLogsDto() {
    }

    public AuditLogsDto(Long id, UsersDto usersDto, String action, String accountNumber, OffsetDateTime createdAt) {
        this.id = id;
        this.usersDto = usersDto;
        this.action = action;
        this.accountNumber = accountNumber;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersDto getUsersDto() {
        return usersDto;
    }

    public void setUsersDto(UsersDto usersDto) {
        this.usersDto = usersDto;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
