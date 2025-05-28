package com.ln.fitness.user_service.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
=======
import lombok.Data;
>>>>>>> 785540e5e5af594c1197227125a04e27d98866a8
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
<<<<<<< HEAD
@Builder
@NoArgsConstructor
@AllArgsConstructor
=======
>>>>>>> 785540e5e5af594c1197227125a04e27d98866a8
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
@Enumerated(EnumType.STRING)
    private UserRole role=UserRole.USER;
@CreationTimestamp
    private LocalDateTime createdAt;
@UpdateTimestamp
    private LocalDateTime updatedAt;
}
