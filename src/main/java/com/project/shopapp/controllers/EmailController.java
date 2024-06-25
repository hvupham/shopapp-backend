package com.project.shopapp.controllers;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Email;
import com.project.shopapp.responses.CheckSocialAccountResponse;
import com.project.shopapp.services.Email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/emails")
public class EmailController {
    private final EmailService emailService;
    @GetMapping("/{id}")
    public ResponseEntity<?> getUesrs(
            @PathVariable("id") Long id
    ) throws DataNotFoundException {
        Email emailResponse = this.emailService.GetEmailById(id);
        return ResponseEntity.ok(emailResponse);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam("email") String email
    ) throws DataNotFoundException {
        if (this.emailService.getUserByEmail(email).getId()<=0){
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