package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Entity.AuditLogsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.AuditLogs;
import com.example.loan_platform.Entity.Users;

import java.util.List;

/*
  Audit log'ları yönetmek için kullanılan servis arayüzüdür.
  Kullanıcı işlemlerinin izlenmesi ve geçmişe dönük incelenebilmesi amacıyla kullanılır.
 */
public interface AuditLogsServiceI {
    // Belirli bir kullanıcı için yapılan işlemi loglar.
    void logAction(UsersDto userDto, String action, String accountNumber);

    // Belirli bir kullanıcıya ait tüm işlem loglarını döndürür.
    List<AuditLogsDto> getLogsByUser(Long userId);

    // Belirli bir hesap numarasına ait tüm işlem loglarını döndürür.
    List<AuditLogsDto> getLogsByAccount(String accountNumber);
}
