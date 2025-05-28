package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody UsersDto usersDto) {
        validateUsersDto(usersDto);  // Manual validation
        usersService.registerUser(usersDto);
    }

    // Kullanıcı bilgilerini ID ile getirme
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<UsersDto> getUserById(@PathVariable Long id) {
        UsersDto userDto = usersService.getUserById(id);
        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return ResponseEntity.ok(userDto);
    }

    // Kullanıcı bilgilerini email ile getirme
    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BANK_OFFICER')")
    public UsersDto getUserByEmail(@PathVariable String email) {
        return usersService.getUserByEmail(email);
    }

    // Kullanıcı güncelleme - Sadece ADMIN veya ilgili kullanıcı yetkisiyle yapılabilir
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER') or @usersService.hasUserPermission(#id, authentication.name)")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable Long id, @RequestBody UsersDto usersDto) {
        validateUsersDto(usersDto);  // Manual validation
        usersService.updateUser(id, usersDto);
    }

    // Kullanıcı silme - Sadece ADMIN yetkisiyle yapılabilir
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id, @RequestParam UserRole userRole) {
        usersService.deleteUser(id, userRole);
    }

    // Manuel doğrulama metodu
    private void validateUsersDto(UsersDto usersDto) {
        if (usersDto.getEmail() == null || usersDto.getEmail().isEmpty()) {
            throw new CustomException("E-posta alanı zorunludur!", HttpStatus.BAD_REQUEST);
        }

        if (usersDto.getPasswordHash() == null || usersDto.getPasswordHash().isEmpty()) {
            throw new CustomException("Şifre alanı zorunludur!", HttpStatus.BAD_REQUEST);
        }

        if (usersDto.getName() == null || usersDto.getName().isEmpty()) {
            throw new CustomException("İsim alanı zorunludur!", HttpStatus.BAD_REQUEST);
        }
    }
}
