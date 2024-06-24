package com.project.shopapp.controllers;

import com.project.shopapp.models.Root;
import com.project.shopapp.responses.GoogleLoginResponse;
import jakarta.annotation.security.PermitAll;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/oauth2")
public class OAuth2Controller {
    @GetMapping("/login/google")
    @PermitAll
    public ResponseEntity<?> currentUserGoogle(
        OAuth2AuthenticationToken oAuth2AuthenticationToken
    ){
        Map<String, Object> map = oAuth2AuthenticationToken.getPrincipal().getAttributes();
        Root root = Root.builder()
                .picture((String) map.get("picture"))
                .name((String) map.get("name"))
                .email((String) map.get("email"))
                .build();

        ModelMapper modelMapper = new ModelMapper();
        GoogleLoginResponse googleLoginResponse = modelMapper.map(root, GoogleLoginResponse.class);

        return ResponseEntity.ok(googleLoginResponse);


    }
}
