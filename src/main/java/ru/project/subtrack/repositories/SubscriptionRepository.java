package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.project.subtrack.models.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findById(UUID subscriptionId);
}
