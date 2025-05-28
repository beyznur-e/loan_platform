package com.example.loan_platform.Service.Interface;

import com.example.loan_platform.DTO.Entity.BankAccountsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.BankAccounts;
import com.example.loan_platform.Entity.Enum.CurrencyAccounts;
import com.example.loan_platform.Entity.Users;

import java.util.List;
import java.util.Optional;

// Banka hesaplarına yönelik işlemleri tanımlayan servis arayüzüdür.
public interface BankAccountsServiceI {
    // Yeni bir banka hesabı oluşturur.
    BankAccountsDto createBankAccounts(UsersDto userDto, String bankName, String accountNumber, String iban, CurrencyAccounts currency);

    // Belirli bir kullanıcıya ait tüm banka hesaplarını döndürür.
    List<BankAccountsDto> getAllAccountsByUser(UsersDto userDto);

    // ID bilgisine göre banka hesabını döndürür.
    BankAccountsDto getBankAccountById(Long id);

    // IBAN numarasına göre banka hesabını döndürür.
    BankAccountsDto getBankAccountByIban(String iban);

    // Hesap numarasına göre banka hesabını döndürür.
    BankAccountsDto getBankAccountByAccountNumber(String accountNumber);
}
