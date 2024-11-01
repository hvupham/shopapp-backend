package com.project.shopapp.controllers;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Email;
import com.project.shopapp.responses.CheckSocialAccountResponse;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.Email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/emails")
@RequiredArgsConstructor

public class EmailController {
    private final EmailService emailService;

    @GetMapping ("/email/{mail}")
    public  ResponseEntity<?> getEmailByEmail(
            HttpServletRequest request,
            @PathVariable("mail") String email
    ){
        return ResponseEntity.ok().body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("ok")
                        .data(emailService.getUserByEmail(email))
                .build());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getUsers(
            @PathVariable("id") Long id
    ) throws DataNotFoundException {
        Email emailResponse = this.emailService.GetEmailById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(emailResponse)
                .message("Get email information successfully")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam("email") String email
    ) throws DataNotFoundException {
        if (this.emailService.getUserByEmail(email).getId() != ""){
            return ResponseEntity.ok(
                    CheckSocialAccountResponse.builder()
                            .message("failed")
                            .build()
            );
        }
        return ResponseEntity.ok(
                CheckSocialAccountResponse.builder()
                        .message("successfully")
                        .quantity(this.emailService.getAllUsers().size())
                        .build()
        );
    }
}