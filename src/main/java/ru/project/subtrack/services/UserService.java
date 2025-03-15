package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Получение текущего пользователя по JWT токену
    public User getCurrentUser(String token) {
        String userIdStr = jwtService.extractUserId(token);
        UUID userId = UUID.fromString(userIdStr);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Обновление данных текущего пользователя
    public User updateCurrentUser(String token, User updatedData) {
        User currentUser = getCurrentUser(token);

        // Обновляем только разрешённые поля (например, name, avatarUrl)
        if (updatedData.getName() != null) {
            currentUser.setName(updatedData.getName());
        }
        if (updatedData.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(updatedData.getAvatarUrl());
        }
        // Можно добавить другие поля по желанию, но email, password, phone обычно не обновляются тут

        return userRepository.save(currentUser); // Сохраняем обновлённого пользователя
    }
}
