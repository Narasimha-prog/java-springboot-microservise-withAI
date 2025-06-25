package com.ln.fitness.gateway;

import com.ln.fitness.gateway.userService.RegisterRequest;
import com.ln.fitness.gateway.userService.UserRole;
import com.ln.fitness.gateway.userService.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSynFilter  implements WebFilter {

    final private UserService userService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest = getUserDetails(token);
        if(userId==null){
            userId=registerRequest.getKeyCloakId();
        }
        if (userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId).flatMap(
                    exist -> {
                        if(!exist) {
                            if(registerRequest != null){
                                return userService.registerUser(registerRequest)
                                .then(Mono.empty());
                            }
                            return Mono.empty();
                        } else {
                            log.info("User is already existed,SkypingSyn");
                            return Mono.empty();
                        }


                    }).then(Mono.defer(() ->
                    {
                        ServerHttpRequest mutedRequest = exchange.getRequest().mutate().
                                header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutedRequest).build());
                    }
            ));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token)
    {
        try
        {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Extract client roles
            Map<String, Object> resourceAccess = (Map<String, Object>) claimsSet.getClaim("resource_access");
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("oath2-clent"); // Use your actual client ID

            List<String> roles = (List<String>) clientAccess.get("roles");
            UserRole userRole = roles.contains("client_admin")
                    ? UserRole.ADMIN
                    : UserRole.USER;
            boolean isAdmin = roles.contains("client_admin");
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
