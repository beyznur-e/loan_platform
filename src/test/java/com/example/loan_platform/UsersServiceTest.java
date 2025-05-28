package com.example.loan_platform;

import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.UsersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UsersServiceTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private CacheManager cacheManager;

    private Users user;
    private UsersDto userDto;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();

        user = new Users();
        user.setName("User");
        user.setEmail("user@example.com");
        user.setPasswordHash("password");
        user.setUserRole(UserRole.CUSTOMER);
        usersRepository.save(user);

        userDto = new UsersDto();
        userDto.setName("Ali seccad");
        userDto.setEmail("aliSeccad2@example.com");
        userDto.setPasswordHash("1234");
        userDto.setUserRole(UserRole.CUSTOMER);
    }

    @Test
    @Transactional
    void testRegisterUser() {
        usersService.registerUser(userDto);

        Users savedUser = usersRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı kaydedilmedi!"));

        assertEquals(userDto.getName(), savedUser.getName());
        assertEquals(userDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void testGetUserById_WhenUserExists() {
        Users savedUser = usersRepository.save(user);

        Long userId = savedUser.getId();

        UsersDto foundUser = usersService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals("User", foundUser.getName());
    }

    @Test
    void testGetUserById_WhenUserNotFound() {
        Long nonExistentId = 999999L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.getUserById(nonExistentId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateUser() {
        Users savedUser = usersRepository.save(user);

        userDto.setName("Updated Name");
        usersService.updateUser(savedUser.getId(), userDto);

        Users updatedUser = usersRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testDeleteUser() {
        usersRepository.save(user);

        usersService.deleteUser(user.getId(), UserRole.CUSTOMER);

        assertFalse(usersRepository.findById(user.getId()).isPresent());
    }

    @Test
    void testGetUserByIdWithCache() {
        usersRepository.deleteAll();

        String uniqueEmail = "john" + UUID.randomUUID() + "@example.com";

        Users mockUser = new Users();
        mockUser.setName("John Doe");
        mockUser.setEmail(uniqueEmail);
        mockUser.setPasswordHash("12345");
        mockUser.setUserRole(UserRole.CUSTOMER);

        Users savedUser = usersRepository.save(mockUser);
        Long userId = savedUser.getId();

        UsersDto first = usersService.getUserById(userId);
        assertEquals("John Doe", first.getName());

        Cache usersCache = cacheManager.getCache("users");
        assertNotNull(usersCache);

        Users cachedUser = usersCache.get(userId, Users.class);
        assertNotNull(cachedUser);

        UsersDto second = usersService.getUserById(userId);
        assertEquals("John Doe", second.getName());

        assertEquals(1, usersRepository.count());
    }

    @AfterEach
    void cleanCache() {
        Cache usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.clear();
        }
    }
}
