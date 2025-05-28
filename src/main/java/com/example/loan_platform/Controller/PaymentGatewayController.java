package com.example.loan_platform.Controller;

import com.example.loan_platform.Service.Interface.PaymentGatewayServiceI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentGatewayController {

    private final PaymentGatewayServiceI paymentGatewayService;

    public PaymentGatewayController(PaymentGatewayServiceI paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/process")
    public ResponseEntity<Boolean> processPayment(@RequestParam Long userId, @RequestParam Double amount) {
        return ResponseEntity.ok(paymentGatewayService.processPayment(userId, amount));
    }
}