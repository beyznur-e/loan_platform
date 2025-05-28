package com.example.loan_platform.Service;

import com.example.loan_platform.Service.Interface.PaymentGatewayServiceI;
import org.springframework.stereotype.Service;

import java.util.Random;

/* Ödeme işlemlerinin simülasyonunu gerçekleştiren sahte (mock) bir servis katmanı.
  Gerçek bir banka veya ödeme sağlayıcısına entegrasyon yapılmadığı için,
  bu sınıf rastgele şekilde başarılı/başarısız sonuç döner.
  Gerçek senaryolarda burası bir banka API'sine REST çağrısı yapacak şekilde yapılandırılır.
  */
@Service
public class PaymentGatewayService implements PaymentGatewayServiceI {
    private final Random random = new Random();

    @Override
    public boolean processPayment(Long userId, Double amount) {
        // Gerçek bir banka API çağrısı yerine rastgele ödeme sonucu dönelim
        return random.nextBoolean();
    }
}

