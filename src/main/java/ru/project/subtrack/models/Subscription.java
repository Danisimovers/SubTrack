package ru.project.subtrack.models;

import jakarta.persistence.*;
import lombok.*;
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
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Дата создания

    @Column()
    private LocalDateTime updatedAt; // Дата последнего обновления

    // Метод для автоматического установления времени при создании подписки
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    // Метод для обновления времени при изменении подписки
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(); // Обновляем только updatedAt при изменении
    }
}
