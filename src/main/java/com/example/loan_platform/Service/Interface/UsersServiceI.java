package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.Users;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

/*
  Kullanıcı yönetimi işlemlerini gerçekleştiren servis arayüzüdür.
  Kayıt, sorgulama, güncelleme ve silme işlemlerini kapsar.
 */
public interface UsersServiceI {
    // Yeni bir kullanıcıyı sisteme kaydeder.
    void registerUser(UsersDto usersDto);

    // Kullanıcıyı ID bilgisine göre getirir.
    UsersDto getUserById(Long id);

    // Kullanıcıyı e-posta adresine göre getirir.
    UsersDto getUserByEmail(String email);

    // Mevcut bir kullanıcının bilgilerini günceller.
    void updateUser(Long id, UsersDto usersDto);

    // Belirli bir kullanıcıyı rol bazlı olarak sistemden siler.
    void deleteUser(Long id, UserRole user_role);
}
