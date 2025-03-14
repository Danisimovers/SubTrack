package ru.project.subtrack.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.models.User;
import ru.project.subtrack.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users") // Все маршруты начинаются с /api/users
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser); // 200 OK + возвращаем созданного пользователя
    }

    // ✅ Получить пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        Optional<User> userOptional = userService.findById(id);
        return userOptional.map(ResponseEntity::ok) // Если пользователь найден
                .orElseGet(() -> ResponseEntity.notFound().build()); // Если нет, вернем 404
    }

    // ✅ Удалить пользователя по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // (Опционально) ✅ Получить всех пользователей
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
