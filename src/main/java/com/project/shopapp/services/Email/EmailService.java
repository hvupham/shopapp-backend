package com.project.shopapp.services.Email;

import com.project.shopapp.dtos.EmailDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Email;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.EmailRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.project.shopapp.models.Role.USER;

@RequiredArgsConstructor
@Service
public class EmailService implements IEmailService{
    private final EmailRepository emailRepository;
//    private final UserService userService;
    @Override
    public Email createUser(EmailDTO emailDTO)  {
        // set id bat dau tu 1
        Integer lastId = emailRepository.findLastIdEmail();
        Integer nextId = (lastId == null) ? 1 : lastId + 1;
        if (emailRepository.findByEmail(emailDTO.getEmail()).isEmpty()){
            Email email  = Email.builder()
                    .id(nextId)
                    .email(emailDTO.getEmail())
                    .name(emailDTO.getName())
                    .picture(emailDTO.getPicture())
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .fullName(emailDTO.getName())
                    .email(emailDTO.getEmail())
                    .googleAccountId(nextId)
                    .roleId(1L)
                    .build();
//            userService.createUser(userDTO);
            return emailRepository.save(email);
        }
        return null;
    }

    @Override
    public Email GetEmailById(long id) throws DataNotFoundException {
        return this.emailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Can not find email with id: "+id));
    }

    @Override
    public List<Email> getAllUsers() {
        return this.emailRepository.findAll();
    }

    @Override
    public void deleteCoupon(long id) {

    }

    @Override
    public Email getUserByEmail(String email) {
        return this.emailRepository.findUserByEmail(email);
    }
}
