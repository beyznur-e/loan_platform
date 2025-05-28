package com.example.loan_platform.Entity;

import com.example.loan_platform.Entity.Enum.Decision;
import jakarta.persistence.*;

@Entity
@Table(name = "loan_scoring")
public class LoanScoring {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id") // Bu satır kritik!
    private LoanApplications application;


    @Column(name = "income")
    private Double income;

    @Column(name = "credit_score")
    private Double creditScore;

    @Column(name = "calculated_score")
    private Double calculatedScore;

    @Enumerated(EnumType.STRING)
    private Decision decision;

    public LoanScoring() {
    }

    public LoanScoring(Long id, LoanApplications application, Double income, Double creditScore, Double calculatedScore, Decision decision) {
        this.id = id;
        this.application = application;
        this.income = income;
        this.creditScore = creditScore;
        this.calculatedScore = calculatedScore;
        this.decision = decision;
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

    public Double getCalculatedScore() {
        return calculatedScore;
    }

    public void setCalculatedScore(Double calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
