package com.example.loan_platform.Controller;

import com.example.loan_platform.DTO.Entity.LoanApplicationsDto;
import com.example.loan_platform.DTO.Entity.NotificationsDto;
import com.example.loan_platform.DTO.Entity.UsersDto;
import com.example.loan_platform.DTO.Request.LoanApplicationsRequestDto;
import com.example.loan_platform.DTO.Response.ApplicationResultDTO;
import com.example.loan_platform.Service.Interface.DocumentsServiceI;
import com.example.loan_platform.Service.Interface.LoanApplicationsServiceI;
import com.example.loan_platform.Service.Interface.NotificationsServiceI;
import com.example.loan_platform.Service.Interface.UsersServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("isAuthenticated()") // Tüm controller'a erişim için login zorunlu
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final UsersServiceI usersService;
    private final LoanApplicationsServiceI loanApplicationsService;
    private final NotificationsServiceI notificationsService;
    private final DocumentsServiceI documentsService;

    public CustomerController(UsersServiceI usersService, LoanApplicationsServiceI loanApplicationsService, NotificationsServiceI notificationsService, DocumentsServiceI documentsService) {
        this.usersService = usersService;
        this.loanApplicationsService = loanApplicationsService;
        this.notificationsService = notificationsService;
        this.documentsService = documentsService;
    }

    // Giriş yapan (authenticated) kullanıcının profilini getirme
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCustomerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Profil görüntüleniyor: {}", userDetails.getUsername());
        return ResponseEntity.ok("Müşteri profili: " + userDetails.getUsername());
    }

    // Kredi başvurusu yapma
    @PostMapping("/apply-loan")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApplicationResultDTO> applyForLoan(@RequestBody LoanApplicationsRequestDto requestDto) {
        logger.info("Kredi başvurusu alındı: Kullanıcı ID {}", requestDto.getUserId());
        logger.info("Kredi başvurusu tamamlandı: Kullanıcı ID {}", requestDto.getUserId());
        return ResponseEntity.ok(loanApplicationsService.applyForLoan(requestDto));
    }

    // Giriş yapan (authenticated) kullanıcının başvurularını getirme
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<LoanApplicationsDto>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Kullanıcının başvuruları getiriliyor: {}", userDetails.getUsername());

        UsersDto userDto = usersService.getUserByEmail(userDetails.getUsername());

        if (userDto == null || userDto.getId() == null) {
            logger.warn("Kullanıcı veya kullanıcı ID bulunamadı: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<LoanApplicationsDto> loanApplicationsDtoList = loanApplicationsService
                .getLoanApplicationsByUser(userDto.getId())
                .stream()
                .peek(dto -> dto.setUser(null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(loanApplicationsDtoList);
    }

    // Giriş yapan (authenticated) kullanıcının bildirimlerini getirme
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationsDto>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Kullanıcının bildirimleri getiriliyor: {}", userDetails.getUsername());

        // Kullanıcıyı DTO olarak alıyoruz
        UsersDto userDto = usersService.getUserByEmail(userDetails.getUsername());

        if (userDto != null) {
            List<NotificationsDto> notifications = notificationsService.getAllNotifications(userDto);
            logger.info("Kullanıcının {} adet bildirimi bulundu: {}", notifications.size(), userDto.getEmail());
            return ResponseEntity.ok(notifications);
        }

        logger.warn("Kullanıcı bulunamadı, bildirim getirilemedi: {}", userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
