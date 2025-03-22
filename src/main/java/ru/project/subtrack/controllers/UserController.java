package ru.project.subtrack.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.dto.UserResponseDTO;
import ru.project.subtrack.dto.UserUpdateDTO;
import ru.project.subtrack.models.User;
import ru.project.subtrack.services.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Получить профиль текущего пользователя
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        User currentUser = userService.getCurrentUser(token);

        // Собираем DTO для ответа
        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .phoneNumber(currentUser.getPhoneNumber())
                .avatarUrl(currentUser.getAvatarUrl())
                .createdAt(currentUser.getCreatedAt().toString())
                .updatedAt(currentUser.getUpdatedAt() != null ? currentUser.getUpdatedAt().toString() : null)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    // Обновить профиль текущего пользователя
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid UserUpdateDTO updateDTO // <-- добавили @Valid
    ) {
        String token = extractToken(authHeader);
        User updatedUser = userService.updateCurrentUser(token, updateDTO);

        // Возвращаем DTO для ответа
        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .phoneNumber(updatedUser.getPhoneNumber())
                .avatarUrl(updatedUser.getAvatarUrl())
                .createdAt(updatedUser.getCreatedAt().toString())
                .updatedAt(updatedUser.getUpdatedAt() != null ? updatedUser.getUpdatedAt().toString() : null)
                .build();

        return ResponseEntity.ok(responseDTO);
    }


    // Вспомогательный метод для извлечения токена из заголовка Authorization
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else {
            throw new RuntimeException("Invalid Authorization header");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestHeader("Authorization") String token, @RequestBody UserUpdateDTO updateDTO) {
        User updatedUser = userService.updateCurrentUser(token.replace("Bearer ", ""), updateDTO);

        // Создаём DTO для возврата
        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .phoneNumber(updatedUser.getPhoneNumber())
                .avatarUrl(updatedUser.getAvatarUrl())
                .createdAt(updatedUser.getCreatedAt().toString())
                .updatedAt(updatedUser.getUpdatedAt().toString())
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
