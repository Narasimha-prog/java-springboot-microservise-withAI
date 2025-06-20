package com.ln.fitness.gateway;

import com.ln.fitness.gateway.userService.RegisterRequest;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSynFilter  implements WebFilter {

    final private UserService userService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (userId != null && token != null) {
            return userService.validateUser(userId).flatMap(
                    exist -> {
                        if (!exist) {
                            //register user

                            RegisterRequest registerRequest = getUserDetails(token);
                            return Mono.empty();
                        } else {
                            log.info("User is already existed,SkypingSyn");
                            return Mono.empty();
                        }


                    }).then(Mono.defer(() ->
                    {
                        ServerHttpRequest mutedRequest = exchange.getRequest().mutate().
                                header("X-User-ID")
                                .build();
                        return chain.filter(exchange.mutate().request(mutedRequest).build());
                    }
            ));
        }
        return null;
    }

    private RegisterRequest getUserDetails(String token)
    {
        try
        {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();


        } catch (Exception e)
        {
            throw new RuntimeException(e);
            return null;
        }

    }

}
