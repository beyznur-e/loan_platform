package com.example.loan_platform.DTO.Entity;

import java.time.OffsetDateTime;

public class DocumentsDto {
    private Long id;
    private UsersDto user;
    private Long applicationId;
    private String documentType;
    private String filePath;
    private OffsetDateTime uploadedAt;

    public DocumentsDto() {
    }

    public DocumentsDto(Long id, UsersDto user, Long applicationId, String documentType, String filePath, OffsetDateTime uploadedAt) {
        this.id = id;
        this.user = user;
        this.applicationId = applicationId;
        this.documentType = documentType;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
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

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public OffsetDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(OffsetDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
