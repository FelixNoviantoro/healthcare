package com.felix.healthcare.api_core.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final KeycloakRoleConverter keycloakRoleConverter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/lab-result").hasRole("admin")
                                .requestMatchers(HttpMethod.POST, "/api/patient").hasRole("admin")
                                .requestMatchers(HttpMethod.POST, "/api/user").hasRole("admin")
                                .requestMatchers("/api/lab-result/upload").hasRole("admin")
                                .anyRequest().hasAnyRole("user", "admin"))
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakRoleConverter)));
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
