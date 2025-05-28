package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Request.LoanApplicationsRequestDto;
import com.example.loan_platform.DTO.Response.ApplicationResultDTO;
import com.example.loan_platform.Service.LoanApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loan_applications")
@PreAuthorize("isAuthenticated()")
public class LoanApplicationsController {

    private final LoanApplicationsService loanApplicationsService;

    @Autowired
    public LoanApplicationsController(LoanApplicationsService loanApplicationsService) {
        this.loanApplicationsService = loanApplicationsService;
    }

    // Başvuru yapma
    @PostMapping("/apply")
    public ApplicationResultDTO applyForLoan(@RequestBody LoanApplicationsRequestDto requestDto) {
        return loanApplicationsService.applyForLoan(requestDto);
    }


    // Kullanıcıya ait tüm başvuruları getirme
    @GetMapping("/user/{userId}")
    @Cacheable(value = "loan_applications", key = "#userId")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or " +
            "(hasRole('CUSTOMER') and @loanApplicationsService.isUserApplicationOwner(#userId, authentication.name))")
    public List<LoanApplicationsDto> getLoanApplicationsByUser(@PathVariable Long userId) {
        return loanApplicationsService.getLoanApplicationsByUser(userId);
    }

    // Başvuru ID'ye göre başvuru detaylarını getirme
    @GetMapping("/{id}")
    @Cacheable(value = "loan_applications", key = "#id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public Optional<LoanApplicationsDto> getLoanApplicationById(@PathVariable Long id) {
        return loanApplicationsService.getLoanApplicationsById(id);
    }

    // Tüm başvuruları getirme
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanApplicationsDto> getAllApplications() {
        return loanApplicationsService.getAllApplications();
    }

    // Başvuruyu onaylama (BANK_OFFICER rolü ile)
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    @PutMapping("/{applicationId}/approve")
    @CacheEvict(value = "loan_applications", key = "#applicationId")
    public void approveLoan(@PathVariable Long applicationId) {
        loanApplicationsService.approveLoan(applicationId);
    }

    // Başvuruyu reddetme (BANK_OFFICER rolü ile)
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    @PutMapping("/{applicationId}/reject")
    @CacheEvict(value = "loan_applications", key = "#applicationId")
    public void rejectLoan(@PathVariable Long applicationId, @RequestParam String reason) {
        loanApplicationsService.rejectLoan(applicationId, reason);
    }
}
