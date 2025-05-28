package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.Notifications;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Messaging.NotificationProducer;
import com.example.loan_platform.Repository.NotificationsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.Interface.UsersServiceI;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

// Kullanıcı servisi: tüm user işlemlerini içerir (register, get, update, delete).
@Service
public class UsersService implements UsersServiceI {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;
    private final AuditLogsService auditLogsService;
    private final NotificationsRepository notificationsRepository;
    private final FakeCreditScoreService fakeCreditScoreService;


    @Autowired
    private NotificationProducer notificationProducer;


    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, AuditLogsService auditLogsService, NotificationsRepository notificationsRepository, FakeCreditScoreService fakeCreditScoreService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogsService = auditLogsService;
        this.notificationsRepository = notificationsRepository;
        this.fakeCreditScoreService = fakeCreditScoreService;
    }

    @Override
    @Transactional
    public void registerUser(UsersDto usersDto) {
        Users user = new Users();
        user.setCreatedAt(OffsetDateTime.now());
        logger.info("Yeni kullanıcı kaydı başlatıldı: {}", usersDto.getEmail());

        if (usersDto.getEmail() == null || usersDto.getPasswordHash() == null) {
            throw new CustomException("E-posta ve şifre alanları zorunludur!", HttpStatus.BAD_REQUEST);
        }

        if (usersRepository.findByEmail(usersDto.getEmail()).isPresent()) {
            throw new CustomException("Bu e-posta adresi zaten kayıtlı!", HttpStatus.CONFLICT);
        }

        try {
            Users users = new Users();
            users.setName(usersDto.getName());
            users.setEmail(usersDto.getEmail());
            users.setPasswordHash(passwordEncoder.encode(usersDto.getPasswordHash()));
            users.setUserRole(usersDto.getUserRole());
            users.setCreatedAt(usersDto.getCreatedAt());
            users.setIncome(usersDto.getIncome());
            // Sahte kredi skoru atama – gerçek senaryoda Findeks, KKB vb. servisler entegre edilir
            double creditScore = fakeCreditScoreService.generateCreditScore(usersDto.getEmail());
            users.setCreditScore(creditScore);
            usersRepository.save(users);
            logger.info("Kullanıcı başarıyla kaydedildi: {}", usersDto.getEmail());


            // ✅ Bildirim gönder (RabbitMQ)
            notificationProducer.sendNotificationEvent(
                    users.getId(),
                    users.getEmail(),
                    "Kaydınız başarıyla tamamlanmıştır."
            );

            UsersDto userDto = new UsersDto();
            userDto.setId(users.getId());
            // ✅ Audit Log Kaydı
            auditLogsService.logAction(userDto, "Kullanıcı başarıyla kaydedildi", null);


        } catch (Exception e) {
            logger.error("Kullanıcı kaydı sırasında hata oluştu: {}", e.getMessage());
            throw new CustomException("Kullanıcı kaydedilemedi! Hata: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UsersDto getUserById(Long id) {
        logger.info("Kullanıcı bilgisi aranıyor: ID={}", id);
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        UsersDto userDto = new UsersDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setUserRole(user.getUserRole());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setIncome(user.getIncome());
        userDto.setCreditScore(user.getCreditScore());

        logger.info("Kullanıcı bilgisi getirildi (ID={})", user.getId());
        return userDto;
    }


    @Override
    @Cacheable(value = "users", key = "#email")
    public UsersDto getUserByEmail(String email) {
        logger.info("Kullanıcı sorgulanıyor (Email={})", maskEmail(email));

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Kullanıcı bulunamadı (Email={})", maskEmail(email));
                    return new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND);
                });

        UsersDto userDto = new UsersDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setUserRole(user.getUserRole());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setIncome(user.getIncome());
        userDto.setCreditScore(user.getCreditScore());

        logger.info("Kullanıcı bilgisi getirildi (ID={})", user.getId());
        return userDto;
    }

    // GDPR gereği hassas veriler loglanmaz – Email maskeleme
    private String maskEmail(String email) {
        if (email == null || email.length() < 5) return "***";
        return email.substring(0, 3) + "***" + email.substring(email.indexOf('@'));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void updateUser(Long id, UsersDto usersDto) {
        logger.info("Kullanıcı kaydı güncelleniyor: {}", usersDto.getEmail());
        if (usersDto.getEmail() == null || usersDto.getPasswordHash() == null) {
            logger.error("Kullanıcı güncelleme başarısız! Eksik bilgi: {}", usersDto);
            throw new CustomException("E-posta ve şifre alanları zorunludur!", HttpStatus.BAD_REQUEST);
        }
        try {
            Users users = usersRepository.findById(id).orElseThrow(() -> new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND));
            users.setName(usersDto.getName());
            users.setPasswordHash(passwordEncoder.encode(usersDto.getPasswordHash()));
            users.setEmail(usersDto.getEmail());
            users.setUserRole(usersDto.getUserRole());
            users.setIncome(usersDto.getIncome());
            double creditScore = fakeCreditScoreService.generateCreditScore(usersDto.getEmail());
            users.setCreditScore(creditScore);
            usersRepository.save(users);
            logger.info("Kullanıcı başarıyla güncellendi: {}", usersDto.getEmail());

            // ✅ Bildirim gönder (RabbitMQ)
            notificationProducer.sendNotificationEvent(
                    users.getId(),
                    users.getEmail(),
                    "Güncellemeniz başarıyla tamamlanmıştır."
            );
            UsersDto userDto = new UsersDto();
            userDto.setId(users.getId());
            // ✅ Audit Log Kaydı
            auditLogsService.logAction(userDto, "Kullanıcı başarıyla güncellendi", null);

        } catch (Exception e) {
            logger.error("Kullanıcı güncellenme sırasında hata oluştu: {}", e.getMessage());
            throw new CustomException("Kullanıcı güncellenemedi! Hata: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')") // RBAC kontrolü – sadece ADMIN kullanıcı siler
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id, UserRole userRole) {
        if (userRole == UserRole.CUSTOMER) {
            logger.warn("CUSTOMER rolü ile kullanıcı silme yetkisi yok! - Kullanıcı ID: {}", id);
            throw new CustomException("Bu işlem için yetkiniz yok!", HttpStatus.FORBIDDEN);
        }

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND));

        notificationsRepository.deleteAllByUserId(id);

        usersRepository.delete(user);

        logger.info("Kullanıcı başarıyla silindi - ID: {}", id);

    }

    // Kullanıcılar yalnızca kendi verilerini güncelleyebilir ve
    // yetkili personel (ADMIN veya BANK_OFFICER) sistem geneli yönetim işlemleri yapabilir.
    public boolean hasUserPermission(Long userId, String email) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND));

        String userEmail = user.getEmail();

        return userEmail.equals(email);
    }

}


