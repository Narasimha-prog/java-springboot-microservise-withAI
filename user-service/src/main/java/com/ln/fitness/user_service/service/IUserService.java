package com.ln.fitness.user_service.service;

import com.ln.fitness.user_service.dto.RegisterRequest;
import com.ln.fitness.user_service.dto.UserResponse;

public interface IUserService {

    UserResponse register(RegisterRequest request);

    UserResponse getUserProfile(String userId);

    Boolean existByKeyCloakId(String userId);
}
