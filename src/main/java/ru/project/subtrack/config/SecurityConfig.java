package ru.project.subtrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/register", // Разрешаем регистрацию без авторизации
                                "/api/users/login",    // Если есть login
                                "/swagger-ui/**",      // Swagger UI
                                "/v3/api-docs/**",     // Swagger docs
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/users/all",
                                "/api/users/**"
                        ).permitAll()
                        .anyRequest().authenticated() // Остальное требует авторизации
                )
                .csrf(csrf -> csrf.disable()); // Отключаем CSRF через новый синтаксис

        return http.build();
    }
}
