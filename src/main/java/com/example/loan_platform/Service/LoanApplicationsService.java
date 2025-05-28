package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.DTO.Request.LoanApplicationsRequestDto;
import com.example.loan_platform.DTO.Response.ApplicationResultDTO;
import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanScoring;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Messaging.NotificationProducer;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.Interface.LoanApplicationsServiceI;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanApplicationsService implements LoanApplicationsServiceI {

    private final LoanApplicationsRepository loanApplicationsRepository;
    private final UsersRepository usersRepository;
    private final LoanScoringService loanScoringService;
    private final AuditLogsService auditLogsService;
    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationsService.class);

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    public LoanApplicationsService(LoanApplicationsRepository loanApplicationsRepository, UsersRepository usersRepository, LoanScoringService loanScoringService, AuditLogsService auditLogsService) {
        this.loanApplicationsRepository = loanApplicationsRepository;
        this.usersRepository = usersRepository;
        this.loanScoringService = loanScoringService;
        this.auditLogsService = auditLogsService;
    }

    @Override
    @Transactional
    public ApplicationResultDTO applyForLoan(LoanApplicationsRequestDto requestDto) {
        logger.info("Yeni başvuru başlatıldı - Kullanıcı ID: {}", requestDto.getUserId());

        Users user = usersRepository.findById(requestDto.getUserId()).orElseThrow(() -> {
            logger.error("Id ile ilgili kullanıcı bulunamadı: {}", requestDto.getUserId());
            return new CustomException("Kullanıcı bulunamadı - ID: " + requestDto.getUserId(), HttpStatus.NOT_FOUND);
        });

        LoanApplications loanApplications = new LoanApplications();
        loanApplications.setUser(user);
        loanApplications.setAmount(requestDto.getAmount());
        loanApplications.setTerm(requestDto.getTerm());
        loanApplications = loanApplicationsRepository.save(loanApplications);

        // ✅ Audit Log Kaydı
        UsersDto userDto = new UsersDto();
        userDto.setId(user.getId());
        auditLogsService.logAction(userDto, "KREDİ BAŞVURUSU YAPILDI", null);

        // ✅ Bildirim gönder (RabbitMQ)
        notificationProducer.sendNotificationEvent(user.getId(), user.getEmail(), "Kredi başvurunuz başarıyla alınmıştır. Başvuru ID: " + loanApplications.getId());

        logger.info("Başvuru değerlendirme işlemi başlatıldı - Başvuru ID: {}", loanApplications.getId());
        return loanScoringService.evaluateLoanApplications(loanApplications.getId());
    }


    @Override
    @Cacheable(value = "loan_applications", key = "#id")
    public Optional<LoanApplicationsDto> getLoanApplicationsById(Long id) {
        logger.info("Başvuru getiriliyor - ID: {}", id);
        Optional<LoanApplications> loanApplications = loanApplicationsRepository.findById(id);
        if (loanApplications.isPresent()) {
            LoanApplications applicationsEntity = loanApplications.get();
            Users user = applicationsEntity.getUser();

            UsersDto usersDto = new UsersDto();
            usersDto.setId(user.getId());
            usersDto.setName(user.getName());
            usersDto.setEmail(user.getEmail());
            usersDto.setUserRole(user.getUserRole());
            usersDto.setIncome(user.getIncome());
            usersDto.setCreditScore(user.getCreditScore()); // Düzeltildi
            usersDto.setCreatedAt(user.getCreatedAt());

            LoanApplicationsDto loanApplicationsDto = new LoanApplicationsDto();
            loanApplicationsDto.setId(applicationsEntity.getId());
            loanApplicationsDto.setAmount(applicationsEntity.getAmount());  // Eklendi
            loanApplicationsDto.setTerm(applicationsEntity.getTerm());      // Eklendi
            loanApplicationsDto.setCreatedAt(applicationsEntity.getCreatedAt());  // Eklendi
            loanApplicationsDto.setUser(usersDto);

            return Optional.of(loanApplicationsDto);
        } else {
            return Optional.empty();
        }
    }


    @Override
    public List<LoanApplicationsDto> getLoanApplicationsByUser(Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND));

        List<LoanApplications> applications = loanApplicationsRepository.findByUser(user);
        List<LoanApplicationsDto> loanApplicationsDto = new ArrayList<>();
        for (LoanApplications applicationsEntity : applications) {
            LoanApplicationsDto dto = new LoanApplicationsDto();
            dto.setId(applicationsEntity.getId());
            dto.setAmount(applicationsEntity.getAmount());
            dto.setTerm(applicationsEntity.getTerm());
            dto.setCreatedAt(applicationsEntity.getCreatedAt());

            UsersDto usersDto = new UsersDto();
            usersDto.setId(user.getId());
            usersDto.setName(user.getName());
            usersDto.setEmail(user.getEmail());
            usersDto.setUserRole(user.getUserRole());
            usersDto.setIncome(user.getIncome());
            usersDto.setCreditScore(user.getCreditScore());
            usersDto.setCreatedAt(user.getCreatedAt());
            dto.setUser(usersDto);

            loanApplicationsDto.add(dto);
        }
        return loanApplicationsDto;
    }


    @Override
    public List<LoanApplicationsDto> getAllApplications() {
        logger.info("Tüm başvurular getiriliyor.");
        List<LoanApplications> loanApplications = loanApplicationsRepository.findAll();
        List<LoanApplicationsDto> loanApplicationsDto = new ArrayList<>();
        for (LoanApplications applicationsEntity : loanApplications) {
            LoanApplicationsDto dto = new LoanApplicationsDto();
            Users user = applicationsEntity.getUser();
            dto.setId(applicationsEntity.getId());
            dto.setAmount(applicationsEntity.getAmount());
            dto.setTerm(applicationsEntity.getTerm());
            dto.setCreatedAt(applicationsEntity.getCreatedAt());

            UsersDto usersDto = new UsersDto();
            usersDto.setId(user.getId());
            usersDto.setName(user.getName());
            usersDto.setEmail(user.getEmail());
            usersDto.setUserRole(user.getUserRole());
            usersDto.setIncome(user.getIncome());
            usersDto.setCreditScore(user.getCreditScore());
            usersDto.setCreatedAt(user.getCreatedAt());
            dto.setUser(usersDto);
            loanApplicationsDto.add(dto);

        }
        return loanApplicationsDto;
    }

    @Override
    @Transactional
    @CacheEvict(value = "loan_applications", key = "#applicationId")
    public void approveLoan(Long applicationId) {
        logger.info("Başvuru onaylanıyor - Başvuru ID: {}", applicationId);

        LoanApplications loanApplications = loanApplicationsRepository.findById(applicationId).orElseThrow(() -> {
            logger.error("Başvuru bulunamadı - ID: {}", applicationId);
            return new CustomException("Başvuru bulunamadı!", HttpStatus.NOT_FOUND);
        });

        // LoanScoring üzerinden decision setle
        LoanScoring loanScoring = loanApplications.getLoanScoring();
        if (loanScoring == null) {
            logger.error("LoanScoring bulunamadı - Başvuru ID: {}", applicationId);
            throw new CustomException("Kredi skorlama verisi bulunamadı!", HttpStatus.NOT_FOUND);
        }

        loanScoring.setDecision(Decision.APPROVED);

        loanApplicationsRepository.save(loanApplications);

        logger.info("Başvuru onaylandı - Başvuru ID: {}", applicationId);

        // ✅ Bildirim gönder (RabbitMQ)
        notificationProducer.sendNotificationEvent(loanApplications.getId(), "Kredi başvurunuz onaylanmıştır. Başvuru ID: " + loanApplications.getId());

        UsersDto userDto = new UsersDto();
        userDto.setId(loanApplications.getUser().getId());

        // ✅ Audit Log Kaydı
        auditLogsService.logAction(userDto, "KREDİ BAŞVURUSU ONAYLANDI", null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "loan_applications", key = "#applicationId")
    public void rejectLoan(Long applicationId, String reason) {
        logger.info("Başvuru reddediliyor - Başvuru ID: {}, Sebep: {}", applicationId, reason);

        LoanApplications loanApplications = loanApplicationsRepository.findById(applicationId).orElseThrow(() -> {
            logger.error("Başvuru bulunamadı - ID: {}", applicationId);
            return new CustomException("Başvuru bulunamadı!", HttpStatus.NOT_FOUND);
        });

        // LoanScoring üzerinden decision setle
        LoanScoring loanScoring = loanApplications.getLoanScoring();
        if (loanScoring == null) {
            logger.error("LoanScoring bulunamadı - Başvuru ID: {}", applicationId);
            throw new CustomException("Kredi skorlama verisi bulunamadı!", HttpStatus.NOT_FOUND);
        }

        loanScoring.setDecision(Decision.REJECTED);

        loanApplicationsRepository.save(loanApplications);
        logger.info("Başvuru reddedildi - Başvuru ID: {}, Sebep: {}", applicationId, reason);

        // ✅ Bildirim gönder (RabbitMQ)
        notificationProducer.sendNotificationEvent(loanApplications.getId(), "Kredi başvurunuz reddedilmiştir. Başvuru ID: " + loanApplications.getId());

        UsersDto userDto = new UsersDto();
        userDto.setId(loanApplications.getUser().getId());
        // ✅ Audit Log Kaydı
        auditLogsService.logAction(userDto, "KREDİ BAŞVURUSU REDDEDİLDİ", null);

    }

    // Verilen userId'nin gerçekten giriş yapan kullanıcıya (email) ait olup olmadığını kontrol eder
    public boolean isUserApplicationOwner(Long userId, String username) {
        Optional<Users> userOptional = usersRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND);
        }

        Users user = userOptional.get();

        return user.getEmail().equals(username); // ← name yerine email
    }

}