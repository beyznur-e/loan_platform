package com.example.loan_platform.DTO.Request;

//Kullanıcı kredi başvurusu yaptığında bunu kullanacağız.
public class LoanApplicationsRequestDto {
    private Long userId;
    private Double amount;
    private Long term;

    public LoanApplicationsRequestDto() {
    }

    public LoanApplicationsRequestDto(Long userId, Double amount, Long term) {
        this.userId = userId;
        this.amount = amount;
        this.term = term;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }
}
