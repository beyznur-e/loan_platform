package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Entity.NotificationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Notifications;
import com.example.loan_platform.Entity.Users;

import java.util.List;

// Kullanıcılara bildirim gönderen ve bildirim geçmişini yöneten servis arayüzüdür.
public interface NotificationsServiceI {
    // Belirli bir kullanıcıya sistem içi bildirim gönderir.
    void sendNotifications(UsersDto userDto, String message);

    // Belirli bir kullanıcının okunmamış tüm bildirimlerini listeler.
    List<NotificationsDto> getUnreadNotifications(UsersDto userDto);

    // Belirli bir kullanıcıya ait tüm bildirimleri döndürür.
    List<NotificationsDto> getAllNotifications(UsersDto userDto);

    // Belirli bir bildirimi "okundu" olarak işaretler.
    void markNotificationsAsRead(Long id);
}
