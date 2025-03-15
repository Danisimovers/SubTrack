package ru.project.subtrack.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.dto.AuthRequest;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        // Проверка: должен быть указан хотя бы email или телефон
        if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank())) {
            return ResponseEntity.badRequest().body("Email or phone number must be provided");
        }

        // Проверка уникальности email, если указан
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already taken");
            }
        }

        // Проверка уникальности телефона, если указан
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                return ResponseEntity.badRequest().body("Phone number already taken");
            }
        }

        // Создание пользователя
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName("DefaultName"); // Можно добавить поле в DTO и сюда пробросить
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Генерация токена (используем email или телефон, что есть)
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber()
        );

        return ResponseEntity.ok(token);
    }

    // Логин
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Проверка: должен быть указан хотя бы email или телефон
        if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank())) {
            return ResponseEntity.badRequest().body("Email or phone number must be provided");
        }

        User user = null;

        // Поиск по email
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user = userRepository.findByEmail(request.getEmail()).orElse(null);
        }

        // Поиск по телефону, если не нашли по email
        if (user == null && request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user = userRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        }

        // Проверка пароля
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email/phone number or password");
        }

        // Генерация токена
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber()
        );

        return ResponseEntity.ok(token);
    }
}
