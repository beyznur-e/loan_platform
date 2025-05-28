package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Response.AuthResponseDTO;
import com.example.loan_platform.DTO.Request.LoginRequestDTO;

// Kimlik doğrulama işlemlerini tanımlayan servis arayüzüdür.
public interface AuthServiceI {
    // Kullanıcının kimlik bilgilerini doğrular ve JWT erişim token'ı üretir.
    AuthResponseDTO login(LoginRequestDTO request);
}
