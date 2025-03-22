package ru.project.subtrack.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.subtrack.dto.SubscriptionDTO;
import ru.project.subtrack.dto.SubscriptionResponseDTO;
import ru.project.subtrack.exceptions.BusinessException;
import ru.project.subtrack.services.SubscriptionService;
import ru.project.subtrack.models.SubscriptionCategory;
import ru.project.subtrack.models.SubscriptionStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // ✅ Получить все подписки текущего пользователя (с фильтрацией)
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getUserSubscriptions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) SubscriptionCategory category,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) List<String> tags // ✅ добавил параметр tags
    ) {
        String token = extractToken(authHeader);
        List<SubscriptionResponseDTO> subscriptions = subscriptionService.getUserSubscriptions(token, category, status, tags);
        return ResponseEntity.ok(subscriptions);
    }

    // ✅ Добавить подписку
    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionDTO subscriptionData
    ) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO created = subscriptionService.createSubscription(token, subscriptionData);
        return ResponseEntity.ok(created);
    }

    // ✅ Обновить подписку
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

    // ✅ Удалить подписку
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
    @PatchMapping("/{id}/status")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscriptionStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestParam SubscriptionStatus status) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO updated = subscriptionService.updateSubscriptionStatus(token, id, status);
        return ResponseEntity.ok(updated);
    }

    // ✅ Полностью заменить теги подписки
    @PatchMapping("/{id}/tags")
    public ResponseEntity<SubscriptionResponseDTO> replaceSubscriptionTags(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody List<String> tags
    ) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO updated = subscriptionService.replaceSubscriptionTags(token, id, tags);
        return ResponseEntity.ok(updated);
    }

    // ✅ Добавить теги без удаления старых
    @PatchMapping("/{id}/tags/add")
    public ResponseEntity<SubscriptionResponseDTO> addSubscriptionTags(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody List<String> tags
    ) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO updated = subscriptionService.addSubscriptionTags(token, id, tags);
        return ResponseEntity.ok(updated);
    }

    // ✅ Удалить определённые теги (не все)
    @PatchMapping("/{id}/tags/remove")
    public ResponseEntity<SubscriptionResponseDTO> removeSubscriptionTags(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody List<String> tags
    ) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO updated = subscriptionService.removeSubscriptionTags(token, id, tags);
        return ResponseEntity.ok(updated);
    }

    // ✅ Получить месячные траты пользователя
    @GetMapping("/analytics/monthly-expenses")
    public ResponseEntity<BigDecimal> getMonthlyExpenses(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        BigDecimal monthlyExpenses = subscriptionService.getMonthlyExpenses(token);
        return ResponseEntity.ok(monthlyExpenses);
    }

    // ✅ Получить годовые траты пользователя
    @GetMapping("/analytics/yearly-expenses")
    public ResponseEntity<BigDecimal> getYearlyExpenses(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        BigDecimal yearlyExpenses = subscriptionService.getYearlyExpenses(token);
        return ResponseEntity.ok(yearlyExpenses);
    }

    // ✅ Получить самую дорогую подписку
    @GetMapping("/analytics/most-expensive")
    public ResponseEntity<SubscriptionResponseDTO> getMostExpensiveSubscription(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO mostExpensive = subscriptionService.getMostExpensiveSubscription(token)
                .orElse(null); // Или бросить исключение, если подписка обязательна
        return ResponseEntity.ok(mostExpensive);
    }

    // ✅ Получить самую дешевую подписку
    @GetMapping("/analytics/cheapest")
    public ResponseEntity<SubscriptionResponseDTO> getCheapestSubscription(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        SubscriptionResponseDTO cheapest = subscriptionService.getCheapestSubscription(token)
                .orElse(null);
        return ResponseEntity.ok(cheapest);
    }

    // ✅ Вспомогательная функция для извлечения токена
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else {
            throw new RuntimeException("Invalid Authorization header");
        }
    }
}
