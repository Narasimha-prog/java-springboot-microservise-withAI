package com.ln.fitness.user_service.service;

import com.ln.fitness.user_service.dto.RegisterRequest;
import com.ln.fitness.user_service.dto.UserResponse;
import com.ln.fitness.user_service.exception.EmailAlreadyExistedException;
import com.ln.fitness.user_service.exception.UserNotFoundException;
import com.ln.fitness.user_service.model.User;
import com.ln.fitness.user_service.repository.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements IUserService{

    private IUserRepository userRepository;


    @Override
    public UserResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
             throw new EmailAlreadyExistedException("Email is Already Existed "+request.getEmail());
//            User existingUser=  userRepository.findByEmail(request.getEmail());
//         return   UserResponse.builder()
//                    .id(existingUser.getId())
//                    .keyCloakId(existingUser.getKeyCloakId())
//                    .email(existingUser.getEmail())
//                    .firstName(existingUser.getFirstName())
//                    .lastName(existingUser.getLastName())
//                    .password(existingUser.getPassword())
//                    .createdAt(existingUser.getCreatedAt())
//                    .updatedAt(existingUser.getUpdatedAt())
//                    .build();
        }
        User user=User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .keyCloakId(request.getKeyCloakId())
                .role(request.getRole())
                .build();
        log.info("User is saved with :{}",user.toString());
     User savedUser=  userRepository.save(user);

     return UserResponse.builder()
             .id(savedUser.getId())
             .keyCloakId(savedUser.getKeyCloakId())
             .email(savedUser.getEmail())
             .firstName(savedUser.getFirstName())
             .lastName(savedUser.getLastName())
             .password(savedUser.getPassword())
             .createdAt(savedUser.getCreatedAt())
             .updatedAt(savedUser.getUpdatedAt())
             .build();

    }

    @Override
    public UserResponse getUserProfile(String userId){
        log.info("Calling GetUser details API for userId {}",userId);
      User user =  userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User is not found with this ID: "+userId) );
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .keyCloakId(user.getKeyCloakId())
                .updatedAt(user.getUpdatedAt())
                .build();

    }

    @Override
    public Boolean existByKeyCloakId(String userId) {
        log.info("Calling User Validation API for userId {}",userId);

        return userRepository.existsByKeyCloakId(userId);
    }
}
