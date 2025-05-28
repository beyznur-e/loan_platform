package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.AuditLogsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Service.Interface.AuditLogsServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit_logs")
@PreAuthorize("isAuthenticated()") // Tüm controller'a erişim için login zorunlu
public class AuditLogsController {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogsController.class);

    private final AuditLogsServiceI auditLogsService;

    public AuditLogsController(AuditLogsServiceI auditLogsService) {
        this.auditLogsService = auditLogsService;
    }

    // Belirli bir kullanıcı için yapılan işlemi loglama
    @PostMapping("/log-action")
    @PreAuthorize("hasAnyRole('ADMIN', 'BANK_OFFICER')")
    public ResponseEntity<String> logAction(
            @RequestBody UsersDto userDto,
            @RequestParam String action,
            @RequestParam String accountNumber) {
        logger.info("Kullanıcı aksiyon kaydı isteniyor: userId={}, action={}, accountNumber={}", userDto.getId(), action, accountNumber);

        try {
            auditLogsService.logAction(userDto, action, accountNumber);
            return ResponseEntity.ok("İşlem başarıyla kaydedildi.");
        } catch (CustomException e) {
            logger.error("LogAction hatası: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }

    // Belirli bir kullanıcıya ait tüm işlem loglarını döndürür.
    @GetMapping("/logs-by-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'BANK_OFFICER')")
    public ResponseEntity<List<AuditLogsDto>> getLogsByUser(@RequestParam Long userId) {
        logger.info("Kullanıcının logları isteniyor: userId={}", userId);

        try {
            List<AuditLogsDto> logs = auditLogsService.getLogsByUser(userId);
            return ResponseEntity.ok(logs);
        } catch (CustomException e) {
            logger.error("getLogsByUser hatası: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(null);
        }
    }

    // Belirli bir hesap numarasına ait tüm işlem loglarını döndürür.
    @GetMapping("/logs-by-account")
    @PreAuthorize("hasAnyRole('ADMIN', 'BANK_OFFICER')")
    public ResponseEntity<List<AuditLogsDto>> getLogsByAccount(@RequestParam String accountNumber) {
        logger.info("Hesap numarasına göre loglar isteniyor: accountNumber={}", accountNumber);

        try {
            List<AuditLogsDto> logs = auditLogsService.getLogsByAccount(accountNumber);
            return ResponseEntity.ok(logs);
        } catch (CustomException e) {
            logger.error("getLogsByAccount hatası: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(null);
        }
    }
}