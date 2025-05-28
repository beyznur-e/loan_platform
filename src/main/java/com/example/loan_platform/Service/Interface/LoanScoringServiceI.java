package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Response.ApplicationResultDTO;

// Kredi skorlama ve başvuru değerlendirme işlemlerini yöneten servis arayüzüdür.
public interface LoanScoringServiceI {
    // Belirli bir kredi başvurusunu değerlendirerek kredi skoru hesaplar ve sonucu döndürür.
    ApplicationResultDTO evaluateLoanApplications(Long applicationId);
}
