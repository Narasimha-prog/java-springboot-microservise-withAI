package com.ln.fitness.gateway;

import com.ln.fitness.gateway.exception.UserServiceUnavailableException;
import com.ln.fitness.gateway.userService.RegisterRequest;
import com.ln.fitness.gateway.userService.UserRole;
import com.ln.fitness.gateway.userService.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.awt.image.ImageProducer;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSynFilter  implements WebFilter {

    final private UserService userService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/actuator/health") || path.equals("/actuator/circuitbreakers")) {
            return chain.filter(exchange);
        }

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || token.isBlank()) {
            log.warn("Authorization token missing, skipping user sync.");
            return chain.filter(exchange);
        }
        RegisterRequest registerRequest = getUserDetails(token);
        if(userId==null){
            userId=registerRequest.getKeyCloakId();
        }
        if (userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId).map(response->response.isSuccess()) .flatMap(
                    exist -> {
                        if (!exist && registerRequest != null) {
                            log.info("User not found, attempting registration.");
                            return userService.registerUser(registerRequest).thenReturn(true);
                        }
                        log.info("User already exists or no register request.");
                        return Mono.just(true);

                    })
                    .onErrorResume(UserServiceUnavailableException.class, ex -> {
                        log.error("User service unavailable during validation or registration", ex);
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE, "User Service is unavailable. Please try again later."));
                    })
                   .flatMap(success -> {
                            ServerHttpRequest mutedRequest = exchange.getRequest().mutate()
                                    .header("X-User-ID", finalUserId)
                                    .build();
                            return chain.filter(exchange.mutate().request(mutedRequest).build());
                        });

        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token)
    {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Extract client roles
            Map<String, Object> resourceAccess = (Map<String, Object>) claimsSet.getClaim("resource_access");
            List<String> roles = null;
            UserRole userRole = null;
            if (resourceAccess != null && resourceAccess.containsKey("oath2-clent")) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("oath2-clent"); // Use your actual client ID
                roles = (List<String>) clientAccess.get("roles");
                log.info("Roles found in oath2-clent: {}", roles);
                userRole = roles.contains("client_admin")
                        ? UserRole.ADMIN
                        : UserRole.USER;
            } else {
                log.warn("Token does not contain 'oath2-clent' in resource_access. Assigning default USER role.");
            }


            return RegisterRequest.builder()
                    .email(claimsSet.getStringClaim("email"))
                    .keyCloakId(claimsSet.getStringClaim("sub"))
                    .password("DummyPassword")
                    .firstName(claimsSet.getStringClaim("given_name"))
                    .lastName(claimsSet.getStringClaim("family_name"))
                    .role(userRole)
                    .build();
        } catch (Exception e)
        {
            throw new RuntimeException(e);

        }


    }

}
