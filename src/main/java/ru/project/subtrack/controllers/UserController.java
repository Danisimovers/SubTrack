package ru.project.subtrack.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.models.User;
import ru.project.subtrack.services.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Получить профиль текущего пользователя
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        User currentUser = userService.getCurrentUser(token);
        return ResponseEntity.ok(currentUser);
    }

    // Обновить профиль текущего пользователя
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody User updatedData
    ) {
        String token = extractToken(authHeader);
        User updatedUser = userService.updateCurrentUser(token, updatedData);
        return ResponseEntity.ok(updatedUser);
    }

    // Вспомогательный метод для извлечения токена из заголовка Authorization
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else {
            throw new RuntimeException("Invalid Authorization header");
        }
    }
}
