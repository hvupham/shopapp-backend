package com.project.shopapp.repositories;

import com.project.shopapp.models.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByEmail(String email);
    Email findUserByEmail(String email);

    @Query("SELECT MAX(m.id) FROM Email m")
    Integer findLastIdEmail();
}
