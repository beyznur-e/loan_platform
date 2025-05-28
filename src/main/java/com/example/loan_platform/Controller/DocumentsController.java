package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.DocumentsDto;
import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.Service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("isAuthenticated()")
public class DocumentsController {

    private final DocumentsService documentsService;

    @Autowired
    public DocumentsController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    // Kullanıcı belgelerini listele
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<List<DocumentsDto>> getDocumentsByUser(@PathVariable Long userId) {
        UsersDto userDto = new UsersDto();
        userDto.setId(userId);
        List<DocumentsDto> documents = documentsService.getDocumentsByUser(userDto);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    // Başvuruya ait belgeleri listele
    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<List<DocumentsDto>> getDocumentsByApplication(@PathVariable Long applicationId) {
        LoanApplicationsDto loanApplicationDto = new LoanApplicationsDto();
        loanApplicationDto.setId(applicationId);
        List<DocumentsDto> documents = documentsService.getDocumentsByApplication(loanApplicationDto);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    // Belge yükle
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<String> uploadDocument(
            @RequestParam Long userId,
            @RequestParam Long loanApplicationId,
            @RequestParam String filePath,
            @RequestParam String documentType) {

        UsersDto userDto = new UsersDto();
        userDto.setId(userId);

        LoanApplicationsDto loanApplicationDto = new LoanApplicationsDto();
        loanApplicationDto.setId(loanApplicationId);

        documentsService.uploadDocument(userDto, loanApplicationDto, filePath, documentType);
        return new ResponseEntity<>("Belge başarıyla yüklendi", HttpStatus.CREATED);
    }

    // Belge silme
    @DeleteMapping("/delete/{documentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<String> deleteDocument(@PathVariable Long documentId) {
        documentsService.deleteDocument(documentId);
        return new ResponseEntity<>("Belge başarıyla silindi", HttpStatus.OK);
    }

    // Belge güncelleme
    @PutMapping("/update/{documentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BANK_OFFICER')")
    public ResponseEntity<String> updateDocument(
            @PathVariable Long documentId,
            @RequestParam String newFilePath,
            @RequestParam String newDocumentType) {

        documentsService.updateDocument(documentId, newFilePath, newDocumentType);
        return new ResponseEntity<>("Belge başarıyla güncellendi", HttpStatus.OK);
    }
}
