package com.project.shopapp.services.Email;

import com.project.shopapp.dtos.EmailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Email;

import java.util.List;

public interface IEmailService {
    Email createUser(EmailDTO emailDTO) throws Exception;
    Email GetEmailById(long id) throws DataNotFoundException;
    List<Email> getAllUsers();
    void deleteCoupon(long id);
    Email getUserByEmail(String email);
}
