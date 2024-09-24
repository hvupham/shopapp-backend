package com.project.shopapp.controllers;
import com.github.javafaker.Faker;
import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.*;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Email;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.comment.CommentResponse;
import com.project.shopapp.services.Email.EmailService;
import com.project.shopapp.services.comment.CommentService;
import com.project.shopapp.services.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/comments")
//@Validated
//Dependency Injection
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final SecurityUtils securityUtils;
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllComments(
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam("product_id") Long productId
    ) {
        List<CommentResponse> commentResponses;
        if (userId == null) {
            commentResponses = commentService.getCommentsByProduct(productId);
        } else {
            commentResponses = commentService.getCommentsByUserAndProduct(userId, productId);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get comments successfully")
                .status(HttpStatus.OK)
                .data(commentResponses)
                .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCommentByProduct(
            @PathVariable ("id") Long productId
    )throws DataNotFoundException {
        List<CommentResponse> commentResponses = commentService.getCommentsByProduct(productId);
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("get all comment by product successfully")
                        .data(commentResponses)
                        .status(HttpStatus.OK)
                .build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateComment(
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) throws Exception {
        User loginUser = securityUtils.getLoggedInUser();
        if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot update another user's comment",
                            HttpStatus.BAD_REQUEST,
                            null));

        }
        commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(
                new ResponseObject(
                        "Update comment successfully",
                        HttpStatus.OK, null));
    }
    @PostMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> insertComment(
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        // Insert the new comment
        User loginUser = securityUtils.getLoggedInUser();
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseObject(
                            "User is not authenticated",
                            HttpStatus.UNAUTHORIZED,
                            null));
        }
        if(loginUser.getId() != commentDTO.getUserId()) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot comment as another user",
                            HttpStatus.BAD_REQUEST,
                            null));
        }
        commentService.insertComment(commentDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Insert comment successfully")
                        .status(HttpStatus.OK)
                        .build());
    }
    @PostMapping("/generateFakeComments")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> generateFakeComments() throws Exception {
        commentService.generateFakeComments();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert fake comments succcessfully")
                .data(null)
                .status(HttpStatus.OK)
                .build());
    }
}
