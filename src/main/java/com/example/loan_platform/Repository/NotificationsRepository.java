package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.Enum.StatusNotifications;
import com.example.loan_platform.Entity.Notifications;
import com.example.loan_platform.Entity.Users;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findByUserAndStatus(Users user, StatusNotifications status);

    List<Notifications> findByUser(Users user); //Kullanıcının tüm bildirimlerini getiren metot

    @Modifying
    @Transactional
    @Query("DELETE FROM Notifications n WHERE n.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
