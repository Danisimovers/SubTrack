package ru.project.subtrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.UserRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Проверяем, есть ли заголовок и начинается ли он с Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Пропускаем, если нет токена
        }

        // Извлекаем токен
        final String jwt = authHeader.startsWith("Bearer ") ? authHeader.substring(7).trim() : authHeader;


        System.out.println("JWT перед валидацией: " + jwt);
        if (!jwtService.validateToken(jwt)) {
            System.out.println("Токен не прошел валидацию!");
            filterChain.doFilter(request, response);
            return;
        }

        // Проверяем токен
        if (!jwtService.validateToken(jwt)) {
            filterChain.doFilter(request, response);
            return; // Пропускаем, если токен невалидный
        }

        // Извлекаем userId
        String userIdStr = jwtService.extractUserId(jwt);

        if (userIdStr == null) {
            filterChain.doFilter(request, response);
            return; // Пропускаем, если не удалось достать userId
        }

        UUID userId = UUID.fromString(userIdStr);

        // Ищем пользователя по userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            filterChain.doFilter(request, response);
            return; // Пропускаем, если пользователь не найден
        }

        // Если всё ок, создаем объект аутентификации
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber())
                .password(user.getPassword()) // пароль не используется, но обязан быть
                .authorities(Collections.emptyList()) // можно добавить роли, если есть
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Устанавливаем аутентификацию в контекст
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Передаем управление дальше
        filterChain.doFilter(request, response);
    }
}
