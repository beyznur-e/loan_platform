package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.AuditLogsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.AuditLogs;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Repository.AuditLogsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.Interface.AuditLogsServiceI;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogsService implements AuditLogsServiceI {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogsService.class);

    private final AuditLogsRepository auditLogsRepository;
    private final UsersRepository usersRepository;

    public AuditLogsService(AuditLogsRepository auditLogsRepository, UsersRepository usersRepository) {
        this.auditLogsRepository = auditLogsRepository;
        this.usersRepository = usersRepository;
    }

    /* NOT: Küçük projelerde setter ile nesne oluşturmak yeterlidir,
       ancak büyük ve karmaşık projelerde Builder Pattern tercih edilir.
       Bu pattern, nesne oluşturmayı daha okunabilir ve güvenli hale getirir.
    */
    @Override
    @Transactional
    public void logAction(UsersDto userDto, String action, String accountNumber) {
        // Kullanıcı bilgilerini UsersDto'dan Users entity'sine dönüştür
        Users user = usersRepository.findById(userDto.getId())
                .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND));

        logger.info("Kullanıcı işlem kaydediliyor. Kullanıcı ID: {}, Aksiyon: {}, Hesap Numarası: {}", user.getId(), action, accountNumber);

        // AuditLogs nesnesi oluşturuluyor
        AuditLogs auditLogs = new AuditLogs();
        auditLogs.setUser(user); // userDto yerine user kullanılıyor
        auditLogs.setAction(action);
        auditLogs.setAccountNumber(accountNumber);

        // Log kaydını veritabanına kaydet
        auditLogsRepository.save(auditLogs);

        logger.info("İşlem kaydedildi. Kullanıcı ID: {}, Aksiyon: {}, Hesap Numarası: {}", user.getId(), action, accountNumber);
    }

    @Override
    public List<AuditLogsDto> getLogsByUser(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND));

        List<AuditLogs> logs = auditLogsRepository.findByUser(user);

        List<AuditLogsDto> auditLogsDtoList = new ArrayList<>();
        for (AuditLogs log : logs) {
            AuditLogsDto dto = new AuditLogsDto();
            dto.setId(log.getId());
            dto.setAction(log.getAction());
            dto.setAccountNumber(log.getAccountNumber());
            dto.setCreatedAt(log.getCreatedAt());

            UsersDto usersDto = new UsersDto();
            usersDto.setId(log.getUser().getId());
            usersDto.setName(log.getUser().getName());
            usersDto.setEmail(log.getUser().getEmail());
            usersDto.setUserRole(log.getUser().getUserRole());
            usersDto.setIncome(log.getUser().getIncome());
            usersDto.setCreditScore(log.getUser().getCreditScore());
            usersDto.setCreatedAt(log.getUser().getCreatedAt());
            dto.setUsersDto(usersDto);

            auditLogsDtoList.add(dto);
        }
        return auditLogsDtoList;
    }


    @Override
    public List<AuditLogsDto> getLogsByAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new CustomException("Geçersiz hesap numarası!", HttpStatus.BAD_REQUEST);
        }

        List<AuditLogs> logs = auditLogsRepository.findByAccountNumber(accountNumber);

        List<AuditLogsDto> auditLogsDtoList = new ArrayList<>();
        for (AuditLogs log : logs) {
            AuditLogsDto dto = new AuditLogsDto();
            dto.setId(log.getId());
            dto.setAction(log.getAction());
            dto.setAccountNumber(log.getAccountNumber());
            dto.setCreatedAt(log.getCreatedAt());

            UsersDto usersDto = new UsersDto();
            usersDto.setId(log.getUser().getId());
            usersDto.setName(log.getUser().getName());
            usersDto.setEmail(log.getUser().getEmail());
            usersDto.setUserRole(log.getUser().getUserRole());
            usersDto.setIncome(log.getUser().getIncome());
            usersDto.setCreditScore(log.getUser().getCreditScore());
            usersDto.setCreatedAt(log.getUser().getCreatedAt());

            dto.setUsersDto(usersDto);

            auditLogsDtoList.add(dto);
        }

        return auditLogsDtoList;
    }
}
