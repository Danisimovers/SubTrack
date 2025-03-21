package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.subtrack.models.Subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findById(UUID subscriptionId);
    List<Subscription> findByUserId(UUID userId);

    // (по желанию) Проверить, существует ли подписка у пользователя
    boolean existsByIdAndUserId(UUID subscriptionId, UUID userId);

    List<Subscription> findByEndDate(LocalDate endDate);

    // (по желанию) Найти по сервису и пользователю (если вдруг понадобится)
    Optional<Subscription> findByServiceNameAndUserId(String serviceName, UUID userId);

    // (по желанию) Удалить только если подписка принадлежит пользователю
    void deleteByIdAndUserId(UUID subscriptionId, UUID userId);
}
