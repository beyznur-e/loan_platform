package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.BankAccountsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Enum.CurrencyAccounts;
import com.example.loan_platform.Service.BankAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank_accounts")
@PreAuthorize("isAuthenticated()")
public class BankAccountsController {

    private final BankAccountsService bankAccountsService;

    @Autowired
    public BankAccountsController(BankAccountsService bankAccountsService) {
        this.bankAccountsService = bankAccountsService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<BankAccountsDto> createBankAccount(
            @RequestParam Long userId,
            @RequestParam String bankName,
            @RequestParam String accountNumber,
            @RequestParam String iban,
            @RequestParam CurrencyAccounts currency) {

        UsersDto userDto = new UsersDto();
        userDto.setId(userId);

        BankAccountsDto createdAccount = bankAccountsService.createBankAccounts(userDto, bankName, accountNumber, iban, currency);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    // Kullanıcıya ait tüm banka hesaplarını listele
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<List<BankAccountsDto>> getAllAccountsByUser(@PathVariable Long userId) {
        UsersDto userDto = new UsersDto();
        userDto.setId(userId);
        List<BankAccountsDto> bankAccounts = bankAccountsService.getAllAccountsByUser(userDto);
        return new ResponseEntity<>(bankAccounts, HttpStatus.OK);
    }


    // ID ile banka hesabını getir
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<BankAccountsDto> getBankAccountById(@PathVariable Long id) {
        BankAccountsDto bankAccount = bankAccountsService.getBankAccountById(id);
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

    // IBAN ile banka hesabını getir
    @GetMapping("/iban")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<BankAccountsDto> getBankAccountByIban(@RequestParam String iban) {
        BankAccountsDto bankAccount = bankAccountsService.getBankAccountByIban(iban);
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

    // Hesap numarası ile banka hesabını getir
    @GetMapping("/account-number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<BankAccountsDto> getBankAccountByAccountNumber(@RequestParam String accountNumber) {
        BankAccountsDto bankAccount = bankAccountsService.getBankAccountByAccountNumber(accountNumber);
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }
}
