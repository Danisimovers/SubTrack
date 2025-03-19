package ru.project.subtrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {

    // Либо email, либо номер телефона - пользователь должен указать хотя бы что-то одно (это проверим в кастомном валидаторе на уровне контроллера/сервиса)

    @Email(message = "Некорректный формат email")
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Некорректный формат номера телефона")
    private String phone;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
}
