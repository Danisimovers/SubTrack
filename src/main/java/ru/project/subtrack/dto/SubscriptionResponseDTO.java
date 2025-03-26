package ru.project.subtrack.dto;

import lombok.Data;
import ru.project.subtrack.models.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class SubscriptionResponseDTO {
    private UUID id;
    private String serviceName;
    private BigDecimal price;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID userId;
    private String userEmail;
    private SubscriptionStatus status; // Текущий статус
    private List<String> tags; // Теги

    // 🔥 Поля для статистики
    private BigDecimal monthlyExpenses; // Месячные траты пользователя
    private BigDecimal yearlyExpenses; // Годовые траты пользователя
    private SubscriptionResponseDTO mostExpensiveSubscription; // Самая дорогая подписка
    private SubscriptionResponseDTO cheapestSubscription; // Самая дешёвая подписка
}
