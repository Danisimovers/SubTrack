package ru.project.subtrack.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.dto.SubscriptionDTO;
import ru.project.subtrack.dto.SubscriptionResponseDTO;
import ru.project.subtrack.services.SubscriptionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // Получить все подписки текущего пользователя
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getUserSubscriptions(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        List<SubscriptionResponseDTO> subscriptions = subscriptionService.getUserSubscriptions(token);
        return ResponseEntity.ok(subscriptions);
    }

    // Добавить подписку
    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionDTO subscriptionData
    ) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO created = subscriptionService.createSubscription(token, subscriptionData);
        return ResponseEntity.ok(created);
    }

    // Обновить подписку
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscription(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody @Valid SubscriptionDTO updatedData
    ) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO updated = subscriptionService.updateSubscription(token, id, updatedData);
        return ResponseEntity.ok(updated);
    }


    // Удалить подписку
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id
    ) {
        String token = extractToken(authHeader);
        subscriptionService.deleteSubscription(token, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trigger-check")
    public String triggerSubscriptionCheck() {
        subscriptionService.checkExpiringSubscriptions();
        return "Проверка подписок запущена!";
    }

    // Вспомогательная функция для извлечения токена
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else {
            throw new RuntimeException("Invalid Authorization header");
        }
    }
}
