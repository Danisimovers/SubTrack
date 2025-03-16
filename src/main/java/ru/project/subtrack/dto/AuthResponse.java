package ru.project.subtrack.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String name;
    private String email;
    private String phoneNumber;
}
