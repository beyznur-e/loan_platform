package com.example.loan_platform.Service.Interface;

/*
  Dış ödeme sistemleri ile entegrasyonu yöneten servis arayüzüdür.
  Ödeme işlemlerinin başlatılması ve sonuçlarının alınmasını sağlar.
 */
public interface PaymentGatewayServiceI {
    // Belirli bir kullanıcı için ödeme işlemini başlatır ve sonucu döndürür.
    boolean processPayment(Long userId, Double amount);
}
