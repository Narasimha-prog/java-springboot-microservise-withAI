package com.ln.fitness.gateway.userService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserErrorResponse {

        private boolean success;
        private String message;
        private String userId;

        // constructors, getters, setters


}
