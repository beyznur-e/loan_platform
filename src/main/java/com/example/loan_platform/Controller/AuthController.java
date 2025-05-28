package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Request.LoginRequestDTO;
import com.example.loan_platform.DTO.Response.AuthResponseDTO;
import com.example.loan_platform.Service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("Kullanıcı için giriş : {}", loginRequest.getEmail());

        try {
            AuthResponseDTO response = authService.login(loginRequest);
            logger.info("Başarılı giriş: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response); // Başarılı login yanıtı

        } catch (BadCredentialsException e) {
            logger.warn("Geçersiz kimlik bilgileri: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        } catch (Exception e) {
            logger.error("Beklenmedik hata: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
