package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.BankAccountsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.BankAccounts;
import com.example.loan_platform.Entity.Enum.CurrencyAccounts;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Messaging.NotificationProducer;
import com.example.loan_platform.Repository.BankAccountsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.Interface.BankAccountsServiceI;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountsService implements BankAccountsServiceI {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountsService.class);

    private final BankAccountsRepository bankAccountsRepository;
    private final UsersRepository usersRepository;
    private final AuditLogsService auditLogsService;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    public BankAccountsService(BankAccountsRepository bankAccountsRepository, UsersRepository usersRepository, AuditLogsService auditLogsService) {
        this.bankAccountsRepository = bankAccountsRepository;
        this.usersRepository = usersRepository;
        this.auditLogsService = auditLogsService;
    }

    @Override
    @Transactional
    @CacheEvict(value = {
            "bankAccountsByUserId",
            "bankAccountById",
            "bankAccountByIban",
            "bankAccountByAccountNumber"
    }, allEntries = true)
    public BankAccountsDto createBankAccounts(UsersDto userDto, String bankName, String accountNumber, String iban, CurrencyAccounts currency) {
        logger.info("Banka hesabı oluşturuluyor. Kullanıcı ID: {}, Banka: {}, IBAN: {}", userDto.getId(), bankName, iban);

        Users user = usersRepository.findById(userDto.getId())
                .orElseThrow(() -> {
                    logger.error("Kullanıcı bulunamadı - ID: {}", userDto.getId());
                    return new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND);
                });

        if (bankAccountsRepository.existsByIban(iban)) {
            throw new CustomException("Bu IBAN zaten kayıtlı!", HttpStatus.CONFLICT);
        }
        if (bankAccountsRepository.existsByAccountNumber(accountNumber)) {
            throw new CustomException("Bu hesap numarası zaten kayıtlı!", HttpStatus.CONFLICT);
        }

        BankAccounts bankAccounts = new BankAccounts();
        bankAccounts.setUser(user);
        bankAccounts.setBankName(bankName);
        bankAccounts.setAccountNumber(accountNumber);
        bankAccounts.setIban(iban);
        bankAccounts.setCurrency(currency);

        BankAccounts savedAccount = bankAccountsRepository.save(bankAccounts);

        logger.info("Banka hesabı başarıyla oluşturuldu. Kullanıcı ID: {}, Hesap ID: {}, IBAN: {}",
                user.getId(),
                savedAccount.getId(),
                iban);

        notificationProducer.sendNotificationEvent(
                user.getId(),
                "Yeni banka hesabınız başarıyla oluşturuldu. IBAN: " + iban
        );

        auditLogsService.logAction(userDto, "BANKA HESABI BAŞARIYLA OLUŞTURULDU", accountNumber);

        return new BankAccountsDto(
                savedAccount.getId(),
                userDto,
                savedAccount.getBankName(),
                savedAccount.getAccountNumber(),
                savedAccount.getIban(),
                savedAccount.getCurrency(),
                savedAccount.getCreatedAt()
        );
    }

    @Override
    @Cacheable(value = "bankAccountsByUserId", key = "#userDto.id")
    public List<BankAccountsDto> getAllAccountsByUser(UsersDto userDto) {
        Users user = usersRepository.findById(userDto.getId())
                .orElseThrow(() -> {
                    logger.error("Kullanıcı bulunamadı - ID: {}", userDto.getId());
                    return new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND);
                });

        List<BankAccounts> bankAccounts = bankAccountsRepository.findAllByUser(user);
        List<BankAccountsDto> bankAccountsDtoArrayList = new ArrayList<>();

        for (BankAccounts bankAccountsEntity : bankAccounts) {
            BankAccountsDto bankAccountsDto = new BankAccountsDto();

            bankAccountsDto.setId(bankAccountsEntity.getId());
            bankAccountsDto.setAccountNumber(bankAccountsEntity.getAccountNumber());
            bankAccountsDto.setBankName(bankAccountsEntity.getBankName());
            bankAccountsDto.setIban(bankAccountsEntity.getIban());
            bankAccountsDto.setCurrency(bankAccountsEntity.getCurrency());

            bankAccountsDto.setUser(userDto); // UsersDto doğrudan atanıyor

            bankAccountsDtoArrayList.add(bankAccountsDto);
        }

        return bankAccountsDtoArrayList;
    }


    @Override
    @Cacheable(value = "bankAccountById", key = "#id")
    public BankAccountsDto getBankAccountById(Long id) {
        BankAccounts bankAccounts = bankAccountsRepository.findById(id)
                .orElseThrow(() -> new CustomException("Hesap bulunamadı!", HttpStatus.NOT_FOUND));

        BankAccountsDto bankAccountsDto = new BankAccountsDto();
        bankAccountsDto.setId(bankAccounts.getId());
        bankAccountsDto.setAccountNumber(bankAccounts.getAccountNumber());
        bankAccountsDto.setBankName(bankAccounts.getBankName());
        bankAccountsDto.setIban(bankAccounts.getIban());
        bankAccountsDto.setCurrency(bankAccounts.getCurrency());

        return bankAccountsDto;
    }

    @Override
    @Cacheable(value = "bankAccountByIban", key = "#iban")
    public BankAccountsDto getBankAccountByIban(String iban) {
        BankAccounts bankAccounts = bankAccountsRepository.findByIban(iban)
                .orElseThrow(() -> new CustomException("Hesap bulunamadı!", HttpStatus.NOT_FOUND));

        BankAccountsDto bankAccountsDto = new BankAccountsDto();
        bankAccountsDto.setId(bankAccounts.getId());
        bankAccountsDto.setAccountNumber(bankAccounts.getAccountNumber());
        bankAccountsDto.setBankName(bankAccounts.getBankName());
        bankAccountsDto.setIban(bankAccounts.getIban());
        bankAccountsDto.setCurrency(bankAccounts.getCurrency());

        return bankAccountsDto;
    }

    @Override
    @Cacheable(value = "bankAccountByAccountNumber", key = "#accountNumber")
    public BankAccountsDto getBankAccountByAccountNumber(String accountNumber) {
        BankAccounts bankAccounts = bankAccountsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomException("Hesap bulunamadı!", HttpStatus.NOT_FOUND));

        BankAccountsDto bankAccountsDto = new BankAccountsDto();
        bankAccountsDto.setId(bankAccounts.getId());
        bankAccountsDto.setAccountNumber(bankAccounts.getAccountNumber());
        bankAccountsDto.setBankName(bankAccounts.getBankName());
        bankAccountsDto.setIban(bankAccounts.getIban());
        bankAccountsDto.setCurrency(bankAccounts.getCurrency());

        return bankAccountsDto;
    }

}
