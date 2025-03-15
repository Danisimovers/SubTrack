package ru.project.subtrack.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionDTO {
    private String serviceName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
