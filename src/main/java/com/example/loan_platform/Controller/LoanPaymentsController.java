package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.LoanPaymentsDto;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Service.LoanPaymentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan_payments")
@PreAuthorize("isAuthenticated()")
public class LoanPaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(LoanPaymentsController.class);

    private final LoanPaymentsService loanPaymentsService;

    @Autowired
    public LoanPaymentsController(LoanPaymentsService loanPaymentsService) {
        this.loanPaymentsService = loanPaymentsService;
    }

    // Ödeme işlemi başlatma
    @PutMapping("/process/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or " +
            "(hasRole('CUSTOMER') and @loanPaymentsService.isUserApplicationOwner(#id, authentication.name))")
    @ResponseStatus(HttpStatus.OK)
    public void processLoanPayment(@PathVariable Long id) {
        try {
            logger.info("Ödeme işlemi başlatılıyor - Payment ID: {}", id);
            loanPaymentsService.processLoanPayment(id);
        } catch (CustomException e) {
            logger.error("Ödeme işlemi sırasında bir hata oluştu: {}", e.getMessage());
            throw e;
        }
    }

    // Ödemeyi işaretleme
    @PutMapping("/mark-paid/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('BANK_OFFICER') or hasRole('ADMIN')")
    public void markPaymentsAsPaid(@PathVariable Long id) {
        try {
            logger.info("Ödeme işaretleniyor - Payment ID: {}", id);
            loanPaymentsService.markPaymentsAsPaid(id);
        } catch (CustomException e) {
            logger.error("Ödeme işaretleme sırasında bir hata oluştu: {}", e.getMessage());
            throw e;
        }
    }

    // Başvuruya ait tüm ödemeleri listeleme
    @GetMapping("/by-application/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = "loan_payments", key = "#applicationId")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('BANK_OFFICER') or hasRole('ADMIN')")
    public List<LoanPaymentsDto> listPaymentsByApplication(@PathVariable Long applicationId) {
        LoanApplications application = new LoanApplications();
        application.setId(applicationId);
        List<LoanPaymentsDto> payments = loanPaymentsService.listPaymentsByApplication(application);
        return payments;
    }

    // Geç ödemeleri listeleme
    @GetMapping("/late-payments")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or (hasRole('CUSTOMER') " +
            "and @loanPaymentsService.hasLatePayments(authentication.name))")
    public List<LoanPaymentsDto> getLatePayments() {
        logger.info("Geç ödemeler getiriliyor...");
        return loanPaymentsService.getLatePayments();
    }
}
