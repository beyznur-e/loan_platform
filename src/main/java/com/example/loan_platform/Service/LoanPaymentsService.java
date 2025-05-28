package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.LoanPaymentsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.StatusPayments;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanPayments;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Messaging.NotificationProducer;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.LoanPaymentsRepository;
import com.example.loan_platform.Service.Interface.LoanPaymentsServiceI;
import com.example.loan_platform.Service.Interface.PaymentGatewayServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanPaymentsService implements LoanPaymentsServiceI {

    private static final Logger logger = LoggerFactory.getLogger(LoanPaymentsService.class);

    private final LoanPaymentsRepository loanPaymentsRepository;
    private final LoanApplicationsRepository loanApplicationsRepository;
    private final PaymentGatewayServiceI paymentGatewayService;
    private final AuditLogsService auditLogsService;

    @Autowired
    NotificationProducer notificationProducer;

    public LoanPaymentsService(LoanPaymentsRepository loanPaymentsRepository, LoanApplicationsRepository loanApplicationsRepository, PaymentGatewayServiceI paymentGatewayService, AuditLogsService auditLogsService) {
        this.loanPaymentsRepository = loanPaymentsRepository;
        this.loanApplicationsRepository = loanApplicationsRepository;
        this.paymentGatewayService = paymentGatewayService;
        this.auditLogsService = auditLogsService;
    }

    // Ödeme yapar
    @Override
    @Transactional
    @CacheEvict(value = "loan_payments", key = "#id")
    public void processLoanPayment(Long id) {
        logger.info("Ödeme işlemi başlatıldı - Payment ID: {}", id);

        LoanPayments loanPayments = loanPaymentsRepository.findById(id).orElseThrow(() -> {
            logger.error("Ödeme bulunamadı - Payment ID: {}", id);
            return new CustomException("Ödeme bulunamadı!", HttpStatus.NOT_FOUND);
        });

        if (loanPayments.getStatus() == StatusPayments.PAID) {
            logger.warn("Ödeme zaten yapılmış - Payment ID: {}", id);
            return; // Zaten ödenmişse işlem yapma
        }

        Long userId = loanPayments.getApplication().getUser().getId();
        Double amount = loanPayments.getAmount();
        logger.debug("Ödeme bilgileri - User ID: {}, Amount: {}", userId, amount);

        // Ödeme, ödeme gateway servisi üzerinden gerçekleştirilir.
        boolean paymentSuccessful = paymentGatewayService.processPayment(userId, amount);

        if (paymentSuccessful) {
            loanPayments.setStatus(StatusPayments.PAID);
            loanPayments.setPaidDate(OffsetDateTime.now());
            loanPaymentsRepository.save(loanPayments);

            // ✅ Bildirim gönder (RabbitMQ)
            notificationProducer.sendNotificationEvent(userId, "Ödeme başarıyla tamamlandı. ");
            logger.info("Ödeme başarıyla tamamlandı - Payment ID: {}, Amount: {}", id, amount);
            UsersDto userDto = new UsersDto();
            userDto.setId(userId);
            // ✅ Audit Log Kaydı
            auditLogsService.logAction(userDto, "ÖDEME BAŞARIYLA TAMAMLANDI", null);

        } else {
            // ✅ Bildirim gönder (RabbitMQ)
            notificationProducer.sendNotificationEvent(userId, "Ödeme başarısız. ");
            logger.error("Ödeme başarısız - Payment ID: {}", id);

            UsersDto userDto = new UsersDto();
            userDto.setId(userId);
            auditLogsService.logAction(userDto, "ÖDEME BAŞARISIZ", null);
            throw new CustomException("Ödeme başarısız.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @CacheEvict(value = "loan_payments", key = "#id")
    public void markPaymentsAsPaid(Long id) {
        logger.info("Ödeme işaretleme başlatıldı - Payment ID: {}", id);

        LoanPayments loanPayments = loanPaymentsRepository.findById(id).orElseThrow(() -> {
            logger.error("Ödeme bulunamadı - Payment ID: {}", id);
            return new CustomException("Ödeme bulunamadı!", HttpStatus.NOT_FOUND);
        });

        if (loanPayments.getStatus() != StatusPayments.PAID) {
            loanPayments.setStatus(StatusPayments.PAID);
            loanPayments.setPaidDate(OffsetDateTime.now());
            loanPaymentsRepository.save(loanPayments);
            logger.info("Ödeme başarıyla işaretlendi - Payment ID: {}", id);
        }
    }


    @Override
    @Cacheable(value = "loan_payments", key = "#application.getId()")
    public List<LoanPaymentsDto> listPaymentsByApplication(LoanApplications application) {
        List<LoanPayments> loanPayments = loanPaymentsRepository.findByApplication(application);

        List<LoanPaymentsDto> loanPaymentsDto = new ArrayList<>();
        for (LoanPayments payment : loanPayments) {
            LoanPaymentsDto dto = new LoanPaymentsDto();
            dto.setId(payment.getId());
            dto.setAmount(payment.getAmount());
            dto.setDueDate(payment.getDueDate());
            dto.setStatus(payment.getStatus());
            dto.setPaidDate(payment.getPaidDate());

            Users userEntity = payment.getApplication().getUser();
            UsersDto userDto = new UsersDto();
            userDto.setId(userEntity.getId());
            userDto.setName(userEntity.getName());
            userDto.setEmail(userEntity.getEmail());
            userDto.setUserRole(userEntity.getUserRole());
            userDto.setIncome(userEntity.getIncome());
            userDto.setCreditScore(userEntity.getCreditScore());

            LoanApplicationsDto applicationDto = new LoanApplicationsDto();
            applicationDto.setUser(userDto);
            applicationDto.setId(payment.getApplication().getId());
            applicationDto.setCreatedAt(payment.getApplication().getCreatedAt());
            applicationDto.setAmount(payment.getApplication().getAmount());
            applicationDto.setTerm(payment.getApplication().getTerm());


            loanPaymentsDto.add(dto);
            dto.setApplication(applicationDto);
        }
        return loanPaymentsDto;
    }


    @Override
    @Cacheable(value = "loan_payments", key = "'latePayments'")
    public List<LoanPaymentsDto> getLatePayments() {
        logger.info("Geç ödemeler getiriliyor...");

        List<LoanPayments> payments = loanPaymentsRepository.findByStatus(StatusPayments.LATE);

        List<LoanPaymentsDto> paymentsDto = new ArrayList<>();

        for (LoanPayments payment : payments) {
            LoanPaymentsDto dto = new LoanPaymentsDto();

            dto.setId(payment.getId());
            dto.setDueDate(payment.getDueDate());
            dto.setAmount(payment.getAmount());
            dto.setStatus(payment.getStatus());
            dto.setPaidDate(payment.getPaidDate());

            Users userEntity = payment.getApplication().getUser();
            UsersDto userDto = new UsersDto();
            userDto.setId(userEntity.getId());
            userDto.setName(userEntity.getName());
            userDto.setEmail(userEntity.getEmail());
            userDto.setUserRole(userEntity.getUserRole());
            userDto.setIncome(userEntity.getIncome());
            userDto.setCreditScore(userEntity.getCreditScore());

            LoanApplicationsDto applicationDto = new LoanApplicationsDto();
            applicationDto.setId(payment.getApplication().getId());
            applicationDto.setUser(userDto);
            applicationDto.setId(payment.getApplication().getId());
            applicationDto.setCreatedAt(payment.getApplication().getCreatedAt());
            applicationDto.setAmount(payment.getApplication().getAmount());
            applicationDto.setTerm(payment.getApplication().getTerm());
            dto.setApplication(applicationDto);

            paymentsDto.add(dto);
        }

        logger.info("Geç ödemeler getirildi: {}", paymentsDto.size());

        return paymentsDto;
    }

    // Ödeme kaydının gerçekten verilen kullanıcıya ait olup olmadığını kontrol eder.
    public boolean isUserApplicationOwner(Long paymentId, String email) {
        LoanPayments payment = loanPaymentsRepository.findById(paymentId).orElseThrow(() -> new CustomException("Ödeme bulunamadı!", HttpStatus.NOT_FOUND));

        String paymentOwnerEmail = payment.getApplication().getUser().getEmail();

        return paymentOwnerEmail.equals(email);
    }

    // Belirtilen isme sahip kullanıcının geç ödemesi olup olmadığını kontrol eder.
    public boolean hasLatePayments(String name) {
        // Kullanıcının en az 1 geç ödemesi varsa true dön
        List<LoanPayments> latePayments = loanPaymentsRepository.findByStatus(StatusPayments.LATE);
        return latePayments.stream().anyMatch(p -> p.getApplication().getUser().getName().equals(name));
    }

}
