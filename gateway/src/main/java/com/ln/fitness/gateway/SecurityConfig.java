package com.ln.fitness.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
 @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity){
         return httpSecurity
     .csrf(ServerHttpSecurity.CsrfSpec::disable)
             .authorizeExchange(exchange -> exchange
                //     .pathMatchers("/actuator/*").permitAll()
                     .anyExchange().authenticated()
                  )
             .oauth2ResourceServer(oAuth2 -> oAuth2
                     .jwt(Customizer.withDefaults()))
             .build();
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
