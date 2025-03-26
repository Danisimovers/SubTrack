package ru.project.subtrack.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.project.subtrack.validations.ValidDateRange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@ValidDateRange // Кастомная валидация: endDate > startDate
public class SubscriptionDTO {

    @NotBlank(message = "Service name cannot be empty")
    private String serviceName;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;


    private List<String> tags; // Опциональные пользовательские метки
}
