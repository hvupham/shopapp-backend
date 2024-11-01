package com.project.shopapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.stereotype.Component;
@Entity
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "emails")
public class Email {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "picture")
    private String picture;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    @NotBlank
    private String email;
}
