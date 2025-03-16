package ru.project.subtrack.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String serviceName; // Например, "Яндекс Плюс", "VK Музыка"

    @Column(nullable = false)
    private BigDecimal price; // Стоимость подписки

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY для оптимизации
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Владелец подписки

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Дата создания

    @Column
    private LocalDateTime updatedAt; // Дата последнего обновления

    // Метод для установки createdAt и updatedAt перед созданием
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    // Метод для обновления updatedAt перед изменением
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
