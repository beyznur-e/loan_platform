package com.example.loan_platform;

import com.example.loan_platform.DTO.Entity.NotificationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.StatusNotifications;
import com.example.loan_platform.Entity.Notifications;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.NotificationsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.NotificationsService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationsServiceTest {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private NotificationsService notificationsService;

    private Users testUser;
    private UsersDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setName("Test User");
        testUser.setEmail("test3@example.com");
        testUser.setPasswordHash("password");
        testUser.setIncome(1000.0);
        testUser = usersRepository.save(testUser);

        testUserDto = new UsersDto();
        testUserDto.setId(testUser.getId());
        testUserDto.setName(testUser.getName());
        testUserDto.setEmail(testUser.getEmail());
        testUserDto.setPasswordHash(testUser.getPasswordHash());
        testUserDto.setIncome(testUser.getIncome());
    }

    @Test
    void testSendNotifications() {
        notificationsService.sendNotifications(testUserDto, "Yeni Bildirim");

        List<Notifications> notifications = notificationsRepository.findByUser(testUser);
        assertEquals(1, notifications.size());
        assertEquals("Yeni Bildirim", notifications.get(0).getMessage());
        assertEquals(StatusNotifications.SENT, notifications.get(0).getStatus());
    }

    @Test
    void testGetUnreadNotifications() {
        notificationsService.sendNotifications(testUserDto, "Bildirim 1");
        notificationsService.sendNotifications(testUserDto, "Bildirim 2");

        List<NotificationsDto> unreadNotifications = notificationsService.getUnreadNotifications(testUserDto);
        assertEquals(2, unreadNotifications.size());
    }

    @Test
    void testGetAllNotifications() {
        notificationsService.sendNotifications(testUserDto, "Bildirim 1");
        notificationsService.sendNotifications(testUserDto, "Bildirim 2");
        notificationsService.sendNotifications(testUserDto, "Bildirim 3");

        List<NotificationsDto> allNotifications = notificationsService.getAllNotifications(testUserDto);
        assertEquals(3, allNotifications.size());
    }

    @Test
    void testMarkNotificationsAsRead() {
        notificationsService.sendNotifications(testUserDto, "Okunacak Bildirim");

        Notifications notification = notificationsRepository.findByUser(testUser).get(0);
        notificationsService.markNotificationsAsRead(notification.getId());

        Notifications updatedNotification = notificationsRepository.findById(notification.getId()).orElse(null);
        assertNotNull(updatedNotification);
        assertEquals(StatusNotifications.READ, updatedNotification.getStatus());
    }
}
