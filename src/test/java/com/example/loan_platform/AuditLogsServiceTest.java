package com.example.loan_platform;

import com.example.loan_platform.DTO.Entity.AuditLogsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.AuditLogs;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.AuditLogsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.AuditLogsService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuditLogsServiceTest {

    @Autowired
    private AuditLogsService auditLogsService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuditLogsRepository auditLogsRepository;

    private Long testUserId;
    private UsersDto testUserDto;

    @BeforeEach
    void setUp() {
        Users user = new Users();
        user.setName("testuser");
        user.setEmail("test1@example.com");
        user.setPasswordHash("password_hash");
        user.setUserRole(UserRole.CUSTOMER);
        user.setIncome(1000.0);
        user = usersRepository.save(user);

        testUserId = user.getId();

        testUserDto = new UsersDto();
        testUserDto.setId(user.getId());
        testUserDto.setName(user.getName());
        testUserDto.setEmail(user.getEmail());
        testUserDto.setPasswordHash(user.getPasswordHash());
        testUserDto.setUserRole(user.getUserRole());
        testUserDto.setIncome(user.getIncome());
    }

    @Test
    void testLogAction() {
        auditLogsService.logAction(testUserDto, "Test Action", "123456789");

        List<AuditLogsDto> logs = auditLogsService.getLogsByUser(testUserId);

        assertFalse(logs.isEmpty());
        assertEquals("Test Action", logs.get(0).getAction());
        assertEquals("123456789", logs.get(0).getAccountNumber());
    }

    @Test
    void testGetLogsByAccount() {
        auditLogsService.logAction(testUserDto, "Deposit", "111111");
        auditLogsService.logAction(testUserDto, "Withdraw", "111111");

        List<AuditLogsDto> logs = auditLogsService.getLogsByAccount("111111");

        assertEquals(2, logs.size());
        assertEquals("Deposit", logs.get(0).getAction());
        assertEquals("Withdraw", logs.get(1).getAction());
    }
}
