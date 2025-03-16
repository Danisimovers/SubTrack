package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.project.subtrack.dto.RegisterRequest;
import ru.project.subtrack.dto.UserUpdateDTO;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;


    // ✅ Получение текущего пользователя по JWT токену
    public User getCurrentUser(String token) {
        String userIdStr = jwtService.extractUserId(token);
        UUID userId = UUID.fromString(userIdStr);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Изменённый метод обновления данных
    public User updateCurrentUser(String token, UserUpdateDTO updatedData) {
        User currentUser = getCurrentUser(token);

        // Обновляем только разрешённые поля
        if (updatedData.getName() != null) {
            currentUser.setName(updatedData.getName());
        }
        if (updatedData.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(updatedData.getAvatarUrl());
        }

        return userRepository.save(currentUser); // Сохраняем обновлённого пользователя
    }
}
