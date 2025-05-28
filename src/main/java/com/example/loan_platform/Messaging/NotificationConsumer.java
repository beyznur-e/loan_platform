package com.example.loan_platform.Messaging;

import com.example.loan_platform.Config.RabbitMQConfig;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ üzerinden gelen bildirim mesajlarını dinleyip işleyen servis sınıfıdır.
 * Gelen mesajlara göre ilgili kullanıcıya bildirim gönderilir.
 */
@Service
public class NotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationsService notificationsService;
    private final UsersRepository usersRepository;

    @Autowired
    public NotificationConsumer(NotificationsService notificationsService,
                                UsersRepository usersRepository) {
        this.notificationsService = notificationsService;
        this.usersRepository = usersRepository;
    }

    // RabbitMQ kuyruğundan gelen NotificationEvent mesajlarını dinler.
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeNotification(NotificationEvent event) {
        logger.info("Yeni bildirim alındı: {}", event.getMessage());

        if (event.getUserId() == null) {
            logger.warn("Bildirim işlenemedi: userId null. Mesaj: {}", event.getMessage());
            return;
        }

        try {
            Users user = usersRepository.findById(event.getUserId())
                    .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

            UsersDto userDto = new UsersDto();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());

            notificationsService.sendNotifications(userDto, event.getMessage());
        } catch (Exception e) {
            logger.error("Bildirim işlenirken hata oluştu: {}", e.getMessage());
        }
    }

}