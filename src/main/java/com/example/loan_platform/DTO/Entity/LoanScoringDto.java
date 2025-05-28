package com.example.loan_platform.DTO.Entity;

import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.LoanApplications;

public class LoanScoringDto {
    private Long id;
    private LoanApplicationsDto application;
    private Double income;
    private Double creditScore;
    private Double calculatedScore;
    private Decision decision;

    public LoanScoringDto() {
    }

    public LoanScoringDto(Long id, LoanApplicationsDto application, Double income, Double creditScore, Double calculatedScore, Decision decision) {
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

    public LoanApplicationsDto getApplication() {
        return application;
    }

    public void setApplication(LoanApplicationsDto application) {
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
