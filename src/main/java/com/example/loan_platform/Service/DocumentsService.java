package com.example.loan_platform.Service;

import com.example.loan_platform.DTO.Entity.DocumentsDto;
import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Documents;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Exception.CustomException;
import com.example.loan_platform.Repository.DocumentsRepository;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.NotificationsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.Interface.DocumentsServiceI;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentsService implements DocumentsServiceI {

    private static final Logger logger = LoggerFactory.getLogger(DocumentsService.class);

    private final DocumentsRepository documentsRepository;
    private final UsersRepository usersRepository;
    private final LoanApplicationsRepository loanApplicationsRepository;
    private final AuditLogsService auditLogsService;
    private final NotificationsRepository notificationsRepository;

    public DocumentsService(DocumentsRepository documentsRepository, UsersRepository usersRepository, LoanApplicationsRepository loanApplicationsRepository, AuditLogsService auditLogsService, NotificationsRepository notificationsRepository) {
        this.documentsRepository = documentsRepository;
        this.usersRepository = usersRepository;
        this.loanApplicationsRepository = loanApplicationsRepository;
        this.auditLogsService = auditLogsService;
        this.notificationsRepository = notificationsRepository;
    }

    @Transactional
    @Override
    public void uploadDocument(UsersDto userDto, LoanApplicationsDto loanApplicationDto, String filePath, String documentType) {
        logger.info("Belge yükleme işlemi başladı - Kullanıcı: {}", userDto.getId());

        Users user = usersRepository.findById(userDto.getId())
                .orElseThrow(() -> {
                    logger.warn("Kullanıcı bulunamadı - ID: {}", userDto.getId());
                    return new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND);
                });

        LoanApplications loanApplication = loanApplicationsRepository.findById(loanApplicationDto.getId())
                .orElseThrow(() -> {
                    logger.warn("Başvuru bulunamadı - ID: {}", loanApplicationDto.getId());
                    return new CustomException("Başvuru bulunamadı!", HttpStatus.NOT_FOUND);
                });

        Documents document = new Documents();
        document.setUser(user);
        document.setApplication(loanApplication);
        document.setFilePath(filePath);
        document.setDocumentType(documentType);
        document.setUploadedAt(OffsetDateTime.now());

        documentsRepository.save(document);
        logger.info("Belge başarıyla yüklendi - Kullanıcı: {}, Başvuru: {}", user.getId(), loanApplication.getId());
        auditLogsService.logAction(userDto, "BELGE BAŞARYLA YÜKLENDİ", null);
    }


    @Override
    @Cacheable(value = "documentsByUser", key = "#usersDto.id")
    public List<DocumentsDto> getDocumentsByUser(UsersDto usersDto) {
        logger.info("Kullanıcının belgeleri getiriliyor: {}", usersDto.getId());

        Users user = usersRepository.findById(usersDto.getId())
                .orElseThrow(() -> {
                    logger.warn("Kullanıcı bulunamadı - ID: {}", usersDto.getId());
                    return new CustomException("Kullanıcı bulunamadı!", HttpStatus.NOT_FOUND);
                });
        List<Documents> documents = documentsRepository.findByUser(user);
        List<DocumentsDto> documentsDtoList = new ArrayList<>();
        for (Documents document : documents) {
            DocumentsDto documentsDto = new DocumentsDto();
            usersDto.setUserRole(user.getUserRole());
            usersDto.setId(user.getId());
            usersDto.setName(user.getName());
            usersDto.setEmail(user.getEmail());
            usersDto.setIncome(user.getIncome());
            usersDto.setCreatedAt(user.getCreatedAt());

            documentsDto.setId(document.getId());
            documentsDto.setUser(usersDto);
            documentsDto.setDocumentType(document.getDocumentType());
            documentsDto.setFilePath(document.getFilePath());
            documentsDto.setUploadedAt(document.getUploadedAt());
            documentsDto.setApplicationId(document.getApplication().getId());

            documentsDtoList.add(documentsDto);
        }
        return documentsDtoList;
    }

    @Override
    @Cacheable(value = "documentsByApplication", key = "#loanApplicationsDto.id")
    public List<DocumentsDto> getDocumentsByApplication(LoanApplicationsDto loanApplicationsDto) {
        logger.info("Başvuru için belgeler getiriliyor: {}", loanApplicationsDto.getId());

        LoanApplications loanApplication = loanApplicationsRepository.findById(loanApplicationsDto.getId())
                .orElseThrow(() -> {
                    logger.warn("Başvuru bulunamadı - ID: {}", loanApplicationsDto.getId());
                    return new CustomException("Başvuru bulunamadı!", HttpStatus.NOT_FOUND);
                });

        List<Documents> documents = documentsRepository.findByApplication(loanApplication);

        List<DocumentsDto> documentsDtoList = new ArrayList<>();
        for (Documents document : documents) {
            DocumentsDto documentsDto = new DocumentsDto();

            documentsDto.setId(document.getId());
            documentsDto.setDocumentType(document.getDocumentType());
            documentsDto.setFilePath(document.getFilePath());
            documentsDto.setUploadedAt(document.getUploadedAt());

            UsersDto usersDto = new UsersDto();
            usersDto.setId(document.getUser().getId());
            usersDto.setEmail(document.getUser().getEmail());
            usersDto.setName(document.getUser().getName());
            usersDto.setUserRole(document.getUser().getUserRole());
            usersDto.setCreditScore(document.getUser().getCreditScore());
            usersDto.setIncome(document.getUser().getIncome());

            documentsDto.setUser(usersDto);

            documentsDto.setApplicationId(loanApplication.getId());

            documentsDtoList.add(documentsDto);
        }

        return documentsDtoList;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"documentsByUser", "documentsByApplication"}, allEntries = true)
    public void updateDocument(Long documentId, String newFilePath, String newDocumentType) {
        logger.info("Belge güncelleme işlemi başladı: {}", documentId);

        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> {
                    logger.error("Belge güncelleme başarısız! Dosya bulunamadı: {}", documentId);
                    return new CustomException("Dosya bulunamadı!", HttpStatus.NOT_FOUND);
                });

        document.setFilePath(newFilePath);
        document.setDocumentType(newDocumentType);
        document.setUploadedAt(OffsetDateTime.now());

        documentsRepository.save(document);
        logger.info("Belge başarıyla güncellendi: {}", documentId);
        UsersDto userDto = new UsersDto();
        userDto.setId(document.getUser().getId());
        auditLogsService.logAction(userDto, "BELGE GÜNCELLENDİ", null);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"documentsByUser", "documentsByApplication"}, allEntries = true)
    public void deleteDocument(Long documentId) {
        logger.info("Belge silme işlemi başladı: {}", documentId);

        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> {
                    logger.warn("Belge silme başarısız! Dosya bulunamadı: {}", documentId);
                    return new CustomException("Dosya bulunamadı!", HttpStatus.NOT_FOUND);
                });
        notificationsRepository.deleteById(documentId);
        documentsRepository.delete(document);
        logger.info("Belge başarıyla silindi");
    }

}
