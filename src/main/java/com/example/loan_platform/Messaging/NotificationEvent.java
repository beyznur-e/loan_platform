package com.example.loan_platform.Messaging;

import java.io.Serializable;

public class NotificationEvent implements Serializable {

    private Long userId;
    private String email;
    private String message;

    public NotificationEvent() {
    }

    public NotificationEvent(Long userId, String email, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }

    public NotificationEvent(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public NotificationEvent(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
