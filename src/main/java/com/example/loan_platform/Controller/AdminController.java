package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Service.Interface.UsersServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UsersServiceI usersService;

    public AdminController(UsersServiceI usersService) {
        this.usersService = usersService;
    }

    // Id ile kullanıcı getirme
    @GetMapping("/users/{id}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable Long id) {
        logger.info("Kullanıcı bilgisi getiriliyor: ID {}", id);

        UsersDto userDto = usersService.getUserById(id);

        if (userDto == null) {
            logger.warn("Kullanıcı bulunamadı: ID {}", id);
            throw new CustomException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND);
        }

        logger.info("Kullanıcı başarıyla bulundu: ID {}", id);
        return ResponseEntity.ok(userDto);
    }

    // Kullanıcı güncelleme
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UsersDto usersDto) {
        logger.info("Kullanıcı güncelleniyor: ID {}", id);
        usersService.updateUser(id, usersDto);
        logger.info("Kullanıcı başarıyla güncellendi: ID {}", id);
        return ResponseEntity.ok("Kullanıcı başarıyla güncellendi.");
    }

}
