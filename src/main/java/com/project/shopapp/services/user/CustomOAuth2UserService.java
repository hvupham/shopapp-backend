package com.project.shopapp.services.user;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.models.Email;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.EmailRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService  {
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailRepository emailRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User =  super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        Email existingEmail = emailRepository.findUserByEmail(email);
//        userService.createUser(UserDTO.builder()
//                        .roleId(1L)
//                        .email(email)
//                        .googleAccountId(existingEmail.getId())
//
//                .build());


        return new User();
    }

}
