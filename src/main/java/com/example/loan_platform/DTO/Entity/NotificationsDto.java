package com.example.loan_platform.DTO.Entity;

import com.example.loan_platform.Entity.Enum.StatusNotifications;

import java.time.OffsetDateTime;

public class NotificationsDto {
    private Long id;
    private UsersDto user;
    private String message;
    private StatusNotifications status;
    private OffsetDateTime createdAt;

    public NotificationsDto() {
    }

    public NotificationsDto(Long id, UsersDto user, String message, StatusNotifications status, OffsetDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.status = status;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StatusNotifications getStatus() {
        return status;
    }

    public void setStatus(StatusNotifications status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
