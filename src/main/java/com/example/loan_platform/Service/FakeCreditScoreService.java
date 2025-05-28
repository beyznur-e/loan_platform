package com.example.loan_platform.Service;

import org.springframework.stereotype.Service;

import java.util.Random;

// Gerçek kredi skor servisi simülasyonu için kullanılan sahte servis.
// 300 ile 850 arasında rastgele bir skor üretir.
@Service
public class FakeCreditScoreService {
    private final Random random = new Random();

    public int generateCreditScore(String email) {
        return random.nextInt(551) + 300; // 300 + (0–550)
    }

}
