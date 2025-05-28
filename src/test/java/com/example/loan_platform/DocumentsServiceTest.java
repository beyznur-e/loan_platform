package com.example.loan_platform;

import com.example.loan_platform.DTO.Entity.DocumentsDto;
import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Entity.Documents;
import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.Users;
import com.example.loan_platform.Repository.DocumentsRepository;
import com.example.loan_platform.Repository.LoanApplicationsRepository;
import com.example.loan_platform.Repository.UsersRepository;
import com.example.loan_platform.Service.DocumentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DocumentsServiceTest {

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private LoanApplicationsRepository loanApplicationsRepository;

    private Users testUser;
    private UsersDto testUserDto;
    private LoanApplications testLoanApplication;
    private LoanApplicationsDto testLoanApplicationDto;

    @BeforeEach
    public void setUp() {
        testUser = new Users();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash("$2a$10$dummyHashForTesting");
        testUser.setUserRole(UserRole.CUSTOMER);
        testUser = usersRepository.save(testUser);

        testLoanApplication = new LoanApplications();
        testLoanApplication.setAmount(10000.0);
        testLoanApplication.setUser(testUser);
        testLoanApplication.setTerm(12L);
        testLoanApplication.setCreatedAt(OffsetDateTime.now());
        testLoanApplication = loanApplicationsRepository.save(testLoanApplication);

        testUserDto = new UsersDto();
        testUserDto.setId(testUser.getId());
        testUserDto.setName(testUser.getName());
        testUserDto.setEmail(testUser.getEmail());
        testUserDto.setPasswordHash(testUser.getPasswordHash());
        testUserDto.setUserRole(testUser.getUserRole());

        testLoanApplicationDto = new LoanApplicationsDto();
        testLoanApplicationDto.setId(testLoanApplication.getId());
        testLoanApplicationDto.setAmount(testLoanApplication.getAmount());
        testLoanApplicationDto.setTerm(testLoanApplication.getTerm());
        testLoanApplicationDto.setCreatedAt(testLoanApplication.getCreatedAt());
    }

    @Test
    public void testUploadDocument() {
        String filePath = "path/to/document.pdf";
        String documentType = "PDF";

        documentsService.uploadDocument(testUserDto, testLoanApplicationDto, filePath, documentType);

        List<Documents> documents = documentsRepository.findByUser(testUser);

        assertFalse(documents.isEmpty());
        Documents uploadedDocument = documents.get(0);
        assertEquals(filePath, uploadedDocument.getFilePath());
        assertEquals(documentType, uploadedDocument.getDocumentType());
        assertNotNull(uploadedDocument.getUploadedAt());
        assertEquals(testLoanApplication.getId(), uploadedDocument.getApplication().getId());
    }

    @Test
    public void testGetDocumentsByUser() {
        String filePath = "path/to/document.pdf";
        String documentType = "PDF";

        documentsService.uploadDocument(testUserDto, testLoanApplicationDto, filePath, documentType);

        List<DocumentsDto> documents = documentsService.getDocumentsByUser(testUserDto);

        assertFalse(documents.isEmpty());
        assertEquals(filePath, documents.get(0).getFilePath());
        assertEquals(documentType, documents.get(0).getDocumentType());
    }

    @Test
    public void testDeleteDocument() {
        String filePath = "path/to/delete_document.pdf";
        String documentType = "PDF";

        documentsService.uploadDocument(testUserDto, testLoanApplicationDto, filePath, documentType);

        List<Documents> documents = documentsRepository.findByUser(testUser);
        Long documentId = documents.get(0).getId();

        documentsService.deleteDocument(documentId);

        assertTrue(documentsRepository.findById(documentId).isEmpty());
    }

    @Test
    public void testUpdateDocument() {
        String filePath = "path/to/update_document.pdf";
        String documentType = "PDF";

        documentsService.uploadDocument(testUserDto, testLoanApplicationDto, filePath, documentType);

        List<Documents> documents = documentsRepository.findByUser(testUser);
        Long documentId = documents.get(0).getId();

        String newFilePath = "path/to/updated_document.pdf";
        String newDocumentType = "Updated PDF";

        documentsService.updateDocument(documentId, newFilePath, newDocumentType);

        Documents updatedDocument = documentsRepository.findById(documentId).get();
        assertEquals(newFilePath, updatedDocument.getFilePath());
        assertEquals(newDocumentType, updatedDocument.getDocumentType());
    }
}
