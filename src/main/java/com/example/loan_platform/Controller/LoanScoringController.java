package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Response.ApplicationResultDTO;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Service.LoanScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan_scoring")
@PreAuthorize("isAuthenticated()")
public class LoanScoringController {

    private static final Logger logger = LoggerFactory.getLogger(LoanScoringController.class);

    private final LoanScoringService loanScoringService;

    public LoanScoringController(LoanScoringService loanScoringService) {
        this.loanScoringService = loanScoringService;
    }

    // Başvuru değerlendirmesi
    @GetMapping("/evaluate/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResultDTO evaluateLoanApplication(@PathVariable Long applicationId) {
        try {
            logger.info("Başvuru değerlendirme başlatıldı: {}", applicationId);
            ApplicationResultDTO result = loanScoringService.evaluateLoanApplications(applicationId);
            if (result == null) {
                throw new CustomException("Başvuru bulunamadı.", HttpStatus.NOT_FOUND);
            }
            return result;
        } catch (CustomException e) {
            logger.error("Başvuru değerlendirme hatası: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Bir hata oluştu: {}", e.getMessage());
            throw new CustomException("Beklenmeyen bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
