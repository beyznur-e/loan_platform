package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Request.LoginRequestDTO;
import com.example.loan_platform.DTO.Response.AuthResponseDTO;
import com.example.loan_platform.Messaging.NotificationProducer;
import com.example.loan_platform.Security.CustomUserDetails;
import com.example.loan_platform.Security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
// Spring Security AuthenticationManager ile kullanıcı kimlik doğrulama işlemi yapılır.
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private NotificationProducer notificationProducer;


    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       CustomUserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            logger.info("Kullanıcı için giriş:  {}", request.getEmail());

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPasswordHash()
                    )
            );

            logger.info("Kimlik doğrulama başarılı: {}", request.getEmail());

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            logger.debug("JWT token oluşturuldu: {}", token);

            return new AuthResponseDTO(token);

        } catch (BadCredentialsException e) {
            logger.warn("Geçersiz giriş: {}", request.getEmail());
            throw e;
        } catch (Exception e) {
            logger.error("Login sırasında hata: {}", request.getEmail(), e);
            throw new RuntimeException("Giriş başarısız", e);
        }
    }
}