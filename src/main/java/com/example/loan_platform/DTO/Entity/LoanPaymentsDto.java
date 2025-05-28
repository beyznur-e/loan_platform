package com.example.loan_platform.DTO.Entity;

import com.example.loan_platform.Entity.Enum.StatusPayments;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class LoanPaymentsDto {
    private Long id;
    private LoanApplicationsDto application;
    private LocalDate dueDate;
    private Double amount;
    private StatusPayments status;
    private OffsetDateTime paidDate;

    public LoanPaymentsDto() {
    }

    public LoanPaymentsDto(Long id, LoanApplicationsDto application, LocalDate dueDate, Double amount, StatusPayments status, OffsetDateTime paidDate) {
        this.id = id;
        this.application = application;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
        this.paidDate = paidDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoanApplicationsDto getApplication() {
        return application;
    }

    public void setApplication(LoanApplicationsDto application) {
        this.application = application;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public StatusPayments getStatus() {
        return status;
    }

    public void setStatus(StatusPayments status) {
        this.status = status;
    }

    public OffsetDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(OffsetDateTime paidDate) {
        this.paidDate = paidDate;
    }
}
