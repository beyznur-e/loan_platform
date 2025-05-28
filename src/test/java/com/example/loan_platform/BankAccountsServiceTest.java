package com.example.loan_platform;

import com.example.loan_platform.DTO.Entity.BankAccountsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.BankAccounts;
import com.example.loan_platform.Entity.Enum.CurrencyAccounts;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.BankAccountsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.BankAccountsService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BankAccountsServiceTest {

    @Autowired
    private BankAccountsService bankAccountsService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BankAccountsRepository bankAccountsRepository;

    private UsersDto testUser;

    @BeforeEach
    public void setUp() {
        Users userEntity = new Users();
        userEntity.setName("Test User");
        userEntity.setEmail("testuser@example.com");
        userEntity.setPasswordHash("$2a$10$dummyHashForTesting");
        userEntity = usersRepository.save(userEntity);

        UsersDto testUser = new UsersDto();
        testUser.setId(userEntity.getId()); // ID önemli, çünkü ilişkilendirmede lazım olur
        testUser.setName(userEntity.getName());
        testUser.setEmail(userEntity.getEmail());
        testUser.setPasswordHash(userEntity.getPasswordHash());

        bankAccountsService.createBankAccounts(
                testUser,
                "Test Bank",
                "123456789",
                "TR00123456789",
                CurrencyAccounts.TRY
        );
    }


    @Test
    void testCreateBankAccounts() {
        bankAccountsService.createBankAccounts(
                testUser,
                "Test Bank 2",
                "987654321",
                "TR00987654321",
                CurrencyAccounts.USD
        );

        List<BankAccountsDto> accounts = bankAccountsService.getAllAccountsByUser(testUser);
        assertEquals(2, accounts.size());
    }

    @Test
    void testGetAllAccountsByUser() {
        List<BankAccountsDto> accounts = bankAccountsService.getAllAccountsByUser(testUser);

        assertEquals(1, accounts.size());
        assertEquals("Test Bank", accounts.get(0).getBankName());
    }

    @Test
    void testGetBankAccountById() {
        List<BankAccountsDto> accounts = bankAccountsService.getAllAccountsByUser(testUser);
        Long accountId = accounts.get(0).getId();

        BankAccountsDto retrievedAccount = bankAccountsService.getBankAccountById(accountId);

        assertNotNull(retrievedAccount);
        assertEquals("Test Bank", retrievedAccount.getBankName());
    }

    @Test
    void testGetBankAccountByIban() {
        BankAccountsDto account = bankAccountsService.getBankAccountByIban("TR00123456789");

        assertNotNull(account);
        assertEquals("Test Bank", account.getBankName());
    }
}
