package com.ln.fitness.user_service.repository;

import com.ln.fitness.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User,String> {
    boolean existsByEmail(String email);

}
