package ru.project.subtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;
}
