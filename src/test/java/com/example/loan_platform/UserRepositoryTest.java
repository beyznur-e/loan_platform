package com.example.loan_platform;

import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UsersRepository userRepository;

    @Test
    void shouldNotAllowDuplicateEmails() {
        Users user1 = new Users();
        user1.setName("Beyzanur");
        user1.setEmail("test@example.com");
        user1.setPasswordHash("hashedPassword");
        user1.setUserRole(UserRole.CUSTOMER);
        userRepository.save(user1);

        Users user2 = new Users();
        user2.setName("Beyzanur2");
        user2.setEmail("test@example.com");
        user2.setPasswordHash("hashedPassword");
        user2.setUserRole(UserRole.CUSTOMER);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
        });
    }
}
