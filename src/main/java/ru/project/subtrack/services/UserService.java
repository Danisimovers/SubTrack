package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service // Говорим Spring, что это сервис
@RequiredArgsConstructor // Lombok автоматически создает конструктор для final полей
public class UserService {

    private final UserRepository userRepository; // Подключаем репозиторий для работы с User

    /**
     * Регистрация нового пользователя.
     * Проверяет, существует ли пользователь с таким email или номером телефона.
     * Если все ок — сохраняет в базу.
     */
    public User register(User user) {
        // Проверка: есть ли пользователь с таким email?
        Optional<User> existingByEmail = userRepository.findByEmail(user.getEmail());
        if (existingByEmail.isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Проверка: есть ли пользователь с таким телефоном?
        Optional<User> existingByPhone = userRepository.findByPhoneNumber(user.getPhoneNumber());
        if (existingByPhone.isPresent()) {
            throw new RuntimeException("Пользователь с таким номером телефона уже существует");
        }

        // Если все проверки прошли — сохраняем пользователя
        return userRepository.save(user);
    }

    /**
     * Поиск пользователя по ID.
     * @param id UUID пользователя
     * @return Optional<User>
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Поиск пользователя по email (для логина, восстановления доступа и т.д.)
     * @param email email пользователя
     * @return Optional<User>
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Поиск пользователя по номеру телефона.
     * @param phoneNumber номер телефона пользователя
     * @return Optional<User>
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    // ✅ Метод для создания (регистрации) пользователя
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // ✅ Метод для получения всех пользователей (опционально)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    /**
     * Удаление пользователя по ID.
     * Если пользователя нет, выбрасывает ошибку.
     * @param id UUID пользователя
     */
    public void deleteUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с таким ID не найден");
        }
        userRepository.deleteById(id);
    }
}
