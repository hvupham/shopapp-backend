package com.project.shopapp.controllers;


import com.project.shopapp.models.Role;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.role.RoleService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/a")
@RequiredArgsConstructor
public class TestController {
    private final RoleService roleService;
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get roles successfully")
                .status(HttpStatus.OK)
                .data(roles)
                .build());
    }
    @GetMapping("/et")
    public int test(){
        return 1;
    }
}
