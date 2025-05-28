package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Entity.LoanPaymentsDto;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanPayments;

import java.util.List;

/*
  Kredi ödeme işlemlerini yöneten servis arayüzüdür.
  Ödeme işleme, geç ödemeleri listeleme ve ödeme güncellemelerini içerir.
 */
public interface LoanPaymentsServiceI {
    // Belirli bir kredi ödemesini işler.
    // Ödeme tahsil edilir ve sistemdeki durumu güncellenir.
    void processLoanPayment(Long id);

    // Belirli bir başvuruya ait tüm ödemeleri "ödenmiş" olarak işaretler.
    void markPaymentsAsPaid(Long id);

    // Belirli bir kredi başvurusuna ait tüm ödeme takvimini getirir.
    List<LoanPaymentsDto> listPaymentsByApplication(LoanApplications application);

    // Vadesi geçmiş ve henüz ödenmemiş kredi ödemelerini listeler.
    List<LoanPaymentsDto> getLatePayments();
}
