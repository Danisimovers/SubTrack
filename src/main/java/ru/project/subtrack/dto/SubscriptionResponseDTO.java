package ru.project.subtrack.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubscriptionResponseDTO {
    private UUID id;
    private String serviceName;
    private BigDecimal price; // Цена подписки
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID userId;
    private String userEmail;
}
