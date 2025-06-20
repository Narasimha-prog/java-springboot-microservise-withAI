package com.ln.fitness.gateway.userService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String keyCloakId;
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email")
    private String email;
    @NotBlank(message = "Password is Required")
    @Size(min = 6,message = "Password must having minimum 6 Characters")
    private String password;
    private String firstName;
    private String lastName;

}
