package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.NotificationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.StatusNotifications;
import com.example.loan_platform.Entity.Notifications;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Repository.NotificationsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.Interface.NotificationsServiceI;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NotificationsService, kullanıcılara bildirim gönderme, okuma ve listeleme işlemlerini yöneten servis katmanıdır.
@Service
public class NotificationsService implements NotificationsServiceI {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsService.class);

    private final NotificationsRepository notificationsRepository;
    private final UsersRepository usersRepository;


    public NotificationsService(NotificationsRepository notificationsRepository, UsersRepository usersRepository) {
        this.notificationsRepository = notificationsRepository;
        this.usersRepository = usersRepository;
    }

    // Kullanıcıya yeni bir bildirim gönderir.
    @Override
    @Transactional
    public void sendNotifications(UsersDto userDto, String message) {
        try {
            Users userEntity = usersRepository.findById(userDto.getId())
                    .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

            Notifications notification = new Notifications();
            notification.setUser(userEntity);
            notification.setMessage(message);
            notification.setStatus(StatusNotifications.SENT);
            notification.setCreatedAt(OffsetDateTime.now());

            notificationsRepository.save(notification);

            logger.info("Bildirim veritabanına kaydedildi: Kullanıcı ID={}", userDto.getId());
        } catch (Exception e) {
            logger.error("Bildirim kaydedilemedi: {}", e.getMessage());
            throw new CustomException("Bildirim kaydedilemedi", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    @Cacheable(value = "notifications", key = "#userDto.id") // user_id kullanıyoruz
    public List<NotificationsDto> getUnreadNotifications(UsersDto userDto) {
        logger.info("Okunmamış mesajlar getiriliyor: {}", userDto.getEmail());

        Users user = new Users();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());

        List<Notifications> notifications = notificationsRepository.findByUserAndStatus(user, StatusNotifications.SENT);

        List<NotificationsDto> notificationsDto = new ArrayList<>();
        for (Notifications notificationsEntity : notifications) {
            NotificationsDto dto = new NotificationsDto();
            dto.setId(notificationsEntity.getId());

            UsersDto dtoUser = new UsersDto();
            dtoUser.setId(notificationsEntity.getUser().getId());
            dtoUser.setName(notificationsEntity.getUser().getName());
            dtoUser.setEmail(notificationsEntity.getUser().getEmail());
            dtoUser.setIncome(notificationsEntity.getUser().getIncome());
            dtoUser.setUserRole(notificationsEntity.getUser().getUserRole());
            dtoUser.setCreatedAt(notificationsEntity.getUser().getCreatedAt());
            dtoUser.setCreditScore(notificationsEntity.getUser().getCreditScore());

            dto.setUser(dtoUser);
            dto.setMessage(notificationsEntity.getMessage());
            dto.setStatus(StatusNotifications.SENT);
            notificationsDto.add(dto);
        }

        return notificationsDto;
    }

    @Override
    @Cacheable(value = "notifications", key = "#userDto.id")
    public List<NotificationsDto> getAllNotifications(UsersDto userDto) {
        logger.info("Tüm mesajlar getiriliyor: {}", userDto.getEmail());
        Users user = new Users();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        List<Notifications> notifications = notificationsRepository.findByUser(user);

        List<NotificationsDto> notificationsDto = new ArrayList<>();
        for (Notifications notificationsEntity : notifications) {
            NotificationsDto dto = new NotificationsDto();
            dto.setId(notificationsEntity.getId());
            // User dönüşümü: Entity → DTO
            UsersDto dtoUser = new UsersDto();
            dtoUser.setId(notificationsEntity.getUser().getId());
            dtoUser.setName(notificationsEntity.getUser().getName());
            dtoUser.setEmail(notificationsEntity.getUser().getEmail());
            dtoUser.setIncome(notificationsEntity.getUser().getIncome());
            dtoUser.setUserRole(notificationsEntity.getUser().getUserRole());
            dtoUser.setCreatedAt(notificationsEntity.getUser().getCreatedAt());
            dtoUser.setCreditScore(notificationsEntity.getUser().getCreditScore());

            dto.setUser(dtoUser);
            dto.setMessage(notificationsEntity.getMessage());
            dto.setStatus(notificationsEntity.getStatus());
            notificationsDto.add(dto);
        }
        return notificationsDto;
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#id")
    public void markNotificationsAsRead(Long id) {
        logger.info("Bildirim okunmuş olarak işaretleniyor. Bildirim ID: {}", id);

        Notifications notifications = notificationsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Bildirim bulunamadı! ID: {}", id);
                    return new CustomException("Bildirim bulunamadı!", HttpStatus.NOT_FOUND);
                });

        notifications.setStatus(StatusNotifications.READ);
        notificationsRepository.save(notifications);

        logger.info("Bildirim başarıyla okunmuş olarak işaretlendi. Bildirim ID: {}", id);
    }

    // Verilen kullanıcı ID'sinin ve username'in eşleşip eşleşmediğini kontrol eder.
    public boolean isNotificationOwner(Long userId, String username) {
        Optional<Users> user = usersRepository.findByEmail(username);
        if (user.isEmpty()) {
            logger.warn("Kullanıcı bulunamadı: {}", username);
            return false;
        }
        boolean owner = user.get().getId().equals(userId);
        logger.debug("isNotificationOwner kontrolü: userId={} paramUserId={}, result={}",
                user.get().getId(), userId, owner);
        return owner;
    }

    // Belirtilen bildirim ID'sine ait bildirimin sahibinin, belirtilen kullanıcı olup olmadığını kontrol eder.
    public boolean isNotificationOwnerByNotificationId(Long notificationId, String name) {
        logger.debug("isNotificationOwnerByNotificationId çağrıldı - notificationId={}, username={}", notificationId, name);

        Notifications notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException("Bildirim bulunamadı", HttpStatus.NOT_FOUND));

        String notificationOwnerEmail = notification.getUser().getEmail();
        boolean result = notificationOwnerEmail.equals(name);

        logger.debug("Bildirim sahibi: {}, İstek yapan: {}, Eşleşme sonucu: {}", notificationOwnerEmail, name, result);

        return result;
    }

}
