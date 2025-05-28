package com.example.loan_platform.Messaging;

import com.example.loan_platform.Config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Bu sınıf, RabbitMQ üzerinden bildirim mesajlarını yayınlamak için kullanılır.
 * Sistemde bir işlem gerçekleştiğinde (örneğin kredi başvurusu onaylandığında),
 * ilgili kullanıcıya bildirim göndermek için NotificationEvent mesajları üretilir.
 */
@Service
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotificationEvent(Long userId, String email, String message) {
        NotificationEvent event = new NotificationEvent(userId, email, message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }

    public void sendNotificationEvent(Long userId, String message) {
        NotificationEvent event = new NotificationEvent(userId, message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}
