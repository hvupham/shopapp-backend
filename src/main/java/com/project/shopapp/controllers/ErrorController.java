package com.project.shopapp.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/error")

public class ErrorController  {

    @GetMapping(" ")
    public String handleError(HttpServletRequest request) {
        // Lấy thông tin về lỗi từ request và xử lý nó tại đây
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404"; // Ví dụ: trả về trang lỗi 404
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500"; // Ví dụ: trả về trang lỗi 500
            }
        }
        return "error"; // Mặc định trả về trang lỗi chung
    }

//    @Override
//    public String getErrorPath() {
//        return "/error";
//    }
}
