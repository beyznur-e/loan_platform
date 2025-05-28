package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Request.LoanApplicationsRequestDto;
import com.example.loan_platform.DTO.Response.ApplicationResultDTO;
import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.Entity.LoanApplications;

import java.util.List;
import java.util.Optional;

/*
  Kredi başvurularını yöneten servis arayüzüdür.
  Başvuru oluşturma, listeleme ve onay/red işlemleri gibi işlemleri tanımlar.
 */
public interface LoanApplicationsServiceI {
    //Kullanıcının kredi başvurusu yapmasını sağlar.
    ApplicationResultDTO applyForLoan(LoanApplicationsRequestDto requestDto);

    //Belirli bir kredi başvurusunu getirir.
    Optional<LoanApplicationsDto> getLoanApplicationsById(Long id);

    //Kullanıcının yaptığı başvuruları listeler.
    List<LoanApplicationsDto> getLoanApplicationsByUser(Long user);

    //Admin için, tüm kredi başvurularını getirir.
    List<LoanApplicationsDto> getAllApplications();

    //Krediyi manuel olarak onaylar (Banka yetkilisi tarafından).
    void approveLoan(Long applicationId);

    //Krediyi reddeder (ve müşteriye bildirim gönderir).
    void rejectLoan(Long applicationId, String reason);

}
