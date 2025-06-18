package com.ln.fitness.user_service.repository;

import com.ln.fitness.user_service.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User,String> {
    boolean existsByEmail(String email);
    boolean existsById(String id);

    boolean existsByKeyCloakId(String userId);

    User findByEmail( String email);
}
