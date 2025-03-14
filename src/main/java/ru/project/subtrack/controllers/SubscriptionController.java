package ru.project.subtrack.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.models.Subscription;
import ru.project.subtrack.services.SubscriptionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions") // Все маршруты начинаются с /api/subscriptions
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // ✅ Создание подписки для пользователя
    @PostMapping("/create/{userId}")
    public ResponseEntity<Subscription> createSubscription(
            @PathVariable UUID userId,
            @RequestBody Subscription subscription) {
        Subscription createdSubscription = subscriptionService.createSubscription(subscription, userId);
        return ResponseEntity.ok(createdSubscription);
    }

    // ✅ Получить все подписки пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getSubscriptionsForUser(@PathVariable UUID userId) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsForUser(userId);
        return ResponseEntity.ok(subscriptions);
    }

    // ✅ Получить подписку по ID
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable UUID id) {
        Optional<Subscription> subscriptionOptional = subscriptionService.findById(id);
        return subscriptionOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Обновить подписку
    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable UUID id,
            @RequestBody Subscription subscription) {
        Subscription updatedSubscription = subscriptionService.updateSubscription(id, subscription);
        return ResponseEntity.ok(updatedSubscription);
    }

    // ✅ Удалить подписку по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable UUID id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
