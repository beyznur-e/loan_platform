package com.example.loan_platform.Entity;

import com.example.loan_platform.Entity.Enum.StatusPayments;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "loan_payments")
public class LoanPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplications application;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private StatusPayments status;

    @Column(name = "paid_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime paidDate;

    public LoanPayments() {
    }

    public LoanPayments(Long id, LoanApplications application, LocalDate dueDate, Double amount, StatusPayments status, OffsetDateTime paidDate) {
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

    public LoanApplications getApplication() {
        return application;
    }

    public void setApplication(LoanApplications application) {
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
