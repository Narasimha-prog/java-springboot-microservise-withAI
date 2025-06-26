package com.ln.fitness.gateway;

import com.ln.fitness.gateway.exception.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/users/actuator/**").hasRole("client_admin")
                        .pathMatchers("/api/activities/actuator/**").hasRole("client_admin")
                        .pathMatchers("/api/recommendations/actuator/**").hasRole("client_admin")
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)  // Register custom AccessDeniedHandler
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null && resourceAccess.containsKey("oath2-clent")) {
                Map<String, Object> client = (Map<String, Object>) resourceAccess.get("oath2-clent");
                if (client.containsKey("roles")) {
                    List<String> clientRoles = (List<String>) client.get("roles");
                    log.info("Roless    ...{}",clientRoles.toString());
                    clientRoles.forEach(role ->
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                }
            }

            return Mono.just(new JwtAuthenticationToken(jwt, new ArrayList<>(authorities)));
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
    CorsConfiguration configuration=new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:8989"));
    configuration.setAllowedMethods(List.of("POST","DELETE","PUT","GET","OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","X-User-ID"));
    configuration.setAllowCredentials(true);





    UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**",configuration);
    return source;
}


}
