package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Entity.DocumentsDto;
import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Documents;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.Users;

import java.util.List;

public interface DocumentsServiceI {
    // Dosya yolunu alıp belgeyi oluşturur
    void uploadDocument(UsersDto user, LoanApplicationsDto loanApplication, String filePath, String documentType);

    // Kullanıcının yüklediği belgeleri getirir
    List<DocumentsDto> getDocumentsByUser(UsersDto usersDto);

    // Bir kredi başvurusu ile ilişkili belgeleri getirir
    List<DocumentsDto> getDocumentsByApplication(LoanApplicationsDto loanApplicationsDto);

    // Belgeyi ID ile siler
    void deleteDocument(Long documentId);

    // Belgeyi günceller (Yeni dosya yolu ve belge tipi)
    void updateDocument(Long documentId, String newFilePath, String newDocumentType);
}
