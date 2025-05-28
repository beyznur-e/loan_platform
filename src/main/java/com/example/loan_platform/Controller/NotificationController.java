package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.NotificationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Messaging.NotificationProducer;
import com.example.loan_platform.Service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationsService notificationsService;
    private final NotificationProducer notificationProducer;

    @Autowired
    public NotificationController(NotificationsService notificationsService,
                                  NotificationProducer notificationProducer) {
        this.notificationsService = notificationsService;
        this.notificationProducer = notificationProducer;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody UsersDto userDto,
                                                   @RequestParam String message) {
        try {
            logger.info("Bildirim gönderme isteği alındı: Kullanıcı ID={}", userDto.getId());

            // RabbitMQ'ya event gönder
            notificationProducer.sendNotificationEvent(
                    userDto.getId(),
                    userDto.getEmail(),
                    message
            );

            return ResponseEntity.ok("Bildirim başarıyla gönderildi.");
        } catch (Exception e) {
            logger.error("Bildirim gönderme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Bildirim gönderilemedi");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or " +
            "(hasRole('CUSTOMER') and @notificationsService.isNotificationOwner(#userId, authentication.name))")
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationsDto>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            UsersDto userDto = new UsersDto();
            userDto.setId(userId);
            return ResponseEntity.ok(notificationsService.getUnreadNotifications(userDto));
        } catch (Exception e) {
            logger.error("Okunmamış bildirimleri getirme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or " +
            "(hasRole('CUSTOMER') and @notificationsService.isNotificationOwner(#userId, authentication.name))")
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<NotificationsDto>> getAllNotifications(@PathVariable Long userId) {
        try {
            UsersDto userDto = new UsersDto();
            userDto.setId(userId);
            return ResponseEntity.ok(notificationsService.getAllNotifications(userDto));
        } catch (Exception e) {
            logger.error("Tüm bildirimleri getirme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or " +
            "(hasRole('CUSTOMER') and @notificationsService.isNotificationOwnerByNotificationId(#id, authentication.name))")
    @PatchMapping("/mark-as-read/{id}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        try {
            notificationsService.markNotificationsAsRead(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Bildirim okunmuş olarak işaretlenemedi: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}