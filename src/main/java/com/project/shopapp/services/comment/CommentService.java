package com.project.shopapp.services.comment;

import com.github.javafaker.Faker;
import com.project.shopapp.controllers.ProductController;
import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.models.Comment;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService{
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public Comment insertComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        Product product = productRepository.findById(commentDTO.getProductId()).orElse(null);
        if (user == null || product == null) {
            throw new IllegalArgumentException("User or product not found");
        }
        Comment newComment = Comment.builder()
                .user(user)
                .product(product)
                .content(commentDTO.getContent())
                .build();
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        commentRepository.save(existingComment);
    }

    @Override
    public List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId) {
        List<Comment> comments = commentRepository.findByUserIdAndProductId(userId, productId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

    @Override
    //@Transactional
    public void generateFakeComments() throws Exception {
        Faker faker = new Faker();
        Random random = new Random();
        // Get all users with roleId = 1
        //List<User> users = userRepository.findByRoleId(1L);
        List<User> users = userRepository.findAll();
        // Get all products
        List<Product> products = productRepository.findAll();
        List<Comment> comments = new ArrayList<>();
        final int totalRecords = 10_000;
        final int batchSize = 1000;
        for (int i = 0; i < totalRecords; i++) {

            // Select a random user and product
            User user = users.get(random.nextInt(users.size()));
            Product product = products.get(random.nextInt(products.size()));

            // Generate a fake comment
            Comment comment = Comment.builder()
                    .content(faker.lorem().sentence())
                    .product(product)
                    .user(user)
                    .build();

            // Set a random created date within the range of 2015 to now
            LocalDateTime startDate = LocalDateTime.of(2015, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.now();
            long randomEpoch = ThreadLocalRandom.current()
                    .nextLong(startDate.toEpochSecond(ZoneOffset.UTC), endDate.toEpochSecond(ZoneOffset.UTC));
            comment.setCreatedAt(LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC));
            // Save the comment
            comments.add(comment);
            if(comments.size() >= batchSize) {
                commentRepository.saveAll(comments);
                comments.clear();
            }
        }
    }

}
