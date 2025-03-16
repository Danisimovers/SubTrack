package ru.project.subtrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    private String email;

    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;
}
