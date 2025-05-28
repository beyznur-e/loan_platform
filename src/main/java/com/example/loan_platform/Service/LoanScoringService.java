package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Response.ApplicationResultDTO;
import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanScoring;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.LoanScoringRepository;
import com.example.loan_platform.Service.Interface.LoanScoringServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoanScoringService implements LoanScoringServiceI {

    private static final Logger logger = LoggerFactory.getLogger(LoanScoringService.class);

    private final LoanScoringRepository loanScoringRepository;
    private final LoanApplicationsRepository loanApplicationsRepository;
    private static final double MAX_INCOME = 100000.0;  // Maksimum gelir
    private static final double MAX_CREDIT_SCORE = 850.0;  // Maksimum kredi skoru


    public LoanScoringService(LoanScoringRepository loanScoringRepository, LoanApplicationsRepository loanApplicationsRepository) {
        this.loanScoringRepository = loanScoringRepository;
        this.loanApplicationsRepository = loanApplicationsRepository;
    }

    // Kredi başvurusunu değerlendirir ve karar verir.
    @Override
    @Transactional
    @Cacheable(value = "loan_scoring", key = "#applicationId")
    public ApplicationResultDTO evaluateLoanApplications(Long applicationId) {
        logger.info("Başvuru alınıyor: {}", applicationId);

        // Başvuruyu al
        Optional<LoanApplications> loanApplicationO = loanApplicationsRepository.findById(applicationId);
        if (loanApplicationO.isEmpty()) {
            logger.error("Başvuru bulunamadı: {}", applicationId);
            throw new CustomException("Başvuru bulunamadı!", HttpStatus.NOT_FOUND);
        }
        LoanApplications loanApplication = loanApplicationO.get();
        logger.debug("Başvuru bulundu: {}", applicationId);

        Users user = loanApplication.getUser();
        double income = user.getIncome();
        double creditScore = user.getCreditScore();

        // Kredi skoru hesapla
        double calculatedScore = calculateCreditScore(income, creditScore);

        LoanScoring loanScoring = new LoanScoring();
        loanScoring.setApplication(loanApplication);
        loanScoring.setIncome(income);
        loanScoring.setCreditScore(creditScore);
        loanScoring.setCalculatedScore(calculatedScore);

        // Kredi skoru 50 ve üzeri ise onay, altı ise red
        if (calculatedScore >= 50) {
            loanScoring.setDecision(Decision.APPROVED);
        } else {
            loanScoring.setDecision(Decision.REJECTED);
        }

        // LoanScoring nesnesini kaydet
        loanScoringRepository.save(loanScoring);
        logger.info("Kredi skoru kaydedildi: Application ID: {}, Skor: {}, Karar: {}",
                applicationId, calculatedScore, loanScoring.getDecision());
        ApplicationResultDTO applicationResultDTO = new ApplicationResultDTO();
        applicationResultDTO.setDecision(String.valueOf(loanScoring.getDecision()));
        return applicationResultDTO;

    }

    // Kredi skoru hesaplama fonksiyonu
    public double calculateCreditScore(double income, double creditScore) {
        logger.debug("Kredi skoru hesaplaması başlatıldı. Gelir: {}, Kredi Skoru: {}", income, creditScore);

        // Gelir faktörü: Gelirin %40'ı
        double incomeFactor = (income / MAX_INCOME) * 100 * 0.4;
        logger.debug("Gelir faktörü hesaplandı: {}", incomeFactor);

        // Kredi skoru faktörü: Kredi skorunun %60'ı
        double creditScoreFactor = (creditScore / MAX_CREDIT_SCORE) * 100 * 0.6;
        logger.debug("Kredi skoru faktörü hesaplandı: {}", creditScoreFactor);

        // Toplam skor hesaplama
        double totalScore = incomeFactor + creditScoreFactor;
        logger.info("Kredi skoru hesaplandı. Gelir Faktörü: {}, Kredi Skoru Faktörü: {}, Toplam Skor: {}",
                incomeFactor, creditScoreFactor, totalScore);

        return totalScore;
    }
}
