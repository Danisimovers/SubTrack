package ru.project.subtrack.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubscriptionResponseDTO {
    private UUID id;
    private String serviceName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID userId;
    private String userEmail;
}
