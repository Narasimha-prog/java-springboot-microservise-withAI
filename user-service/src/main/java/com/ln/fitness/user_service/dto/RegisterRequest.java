package com.ln.fitness.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email")
    private String email;
    @NotBlank(message = "Password is Required")
    @Size(min = 6,message = "Password must having minimum 6 Characters")
    private String password;
    private String firstName;
    private String lastName;

}
