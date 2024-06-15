package com.project.shopapp.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Favorite;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.BaseResponse;
import com.project.shopapp.responses.comment.CommentResponse;
import com.project.shopapp.responses.favorite.FavoriteResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    // Thêm trường totalPages
    private int totalPages;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("comments")
    private List<CommentResponse> comments = new ArrayList<>();

    @JsonProperty("favorites")
    private List<FavoriteResponse> favorites = new ArrayList<>();

    @JsonProperty("category_id")
    private Long categoryId;
    public static ProductResponse fromProduct(Product product) {
        List<Comment> comments = product.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed()) // Sort comments by createdAt in descending order
                .collect(Collectors.toList());
        List<Favorite> favorites = product.getFavorites();
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .comments(comments.stream().map(CommentResponse::fromComment).toList()) // Collect sorted comments into a list
                .favorites(favorites.stream().map(FavoriteResponse::fromFavorite).toList())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .productImages(product.getProductImages())
                .totalPages(0)
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
