package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.subtrack.dto.SubscriptionDTO;
import ru.project.subtrack.dto.SubscriptionResponseDTO;
import ru.project.subtrack.exceptions.BusinessException;
import ru.project.subtrack.models.Subscription;
import ru.project.subtrack.models.SubscriptionCategory;
import ru.project.subtrack.models.SubscriptionStatus;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.SubscriptionRepository;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // ✅ Получить все подписки текущего пользователя (с фильтрацией)
    public List<SubscriptionResponseDTO> getUserSubscriptions(String token, SubscriptionCategory category, SubscriptionStatus status, List<String> tags) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);

        return subscriptions.stream()
                .filter(sub -> category == null || sub.getCategory() == category)
                .filter(sub -> status == null || sub.getStatus() == status)
                .filter(sub -> tags == null || tags.isEmpty() || sub.getTags().stream().anyMatch(tags::contains))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    // ✅ Создать подписку для текущего пользователя
    public SubscriptionResponseDTO createSubscription(String token, SubscriptionDTO subscriptionData) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (subscriptionRepository.findByServiceNameAndUserId(subscriptionData.getServiceName(), userId).isPresent()) {
            throw new BusinessException("Subscription to this service already exists");
        }

        if (subscriptionData.getEndDate().isBefore(subscriptionData.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        if (subscriptionData.getPrice() == null || subscriptionData.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }

        Subscription subscription = Subscription.builder()
                .serviceName(subscriptionData.getServiceName())
                .startDate(subscriptionData.getStartDate())
                .endDate(subscriptionData.getEndDate())
                .price(subscriptionData.getPrice())
                .category(subscriptionData.getCategory())
                .tags(subscriptionData.getTags())
                .status(calculateStatus(subscriptionData.getEndDate()))
                .user(user)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToDTO(saved);
    }

    // ✅ Обновить подписку
    public SubscriptionResponseDTO updateSubscription(String token, UUID subscriptionId, SubscriptionDTO updatedData) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }

        if (updatedData.getEndDate().isBefore(updatedData.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        if (updatedData.getPrice() == null || updatedData.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }

        existing.setServiceName(updatedData.getServiceName());
        existing.setStartDate(updatedData.getStartDate());
        existing.setEndDate(updatedData.getEndDate());
        existing.setPrice(updatedData.getPrice());
        existing.setCategory(updatedData.getCategory());
        existing.setTags(updatedData.getTags());
        existing.setStatus(calculateStatus(updatedData.getEndDate()));

        Subscription updated = subscriptionRepository.save(existing);
        return mapToDTO(updated);
    }

    // ✅ Удалить подписку
    public void deleteSubscription(String token, UUID subscriptionId) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }

        subscriptionRepository.delete(existing);
    }

    // ✅ Маппер в DTO
    private SubscriptionResponseDTO mapToDTO(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setServiceName(subscription.getServiceName());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setUserId(subscription.getUser().getId());
        dto.setUserEmail(subscription.getUser().getEmail());
        dto.setPrice(subscription.getPrice());
        dto.setCategory(subscription.getCategory());
        dto.setStatus(subscription.getStatus());
        dto.setTags(subscription.getTags());
        return dto;
    }

    // ✅ Определение статуса подписки
    private SubscriptionStatus calculateStatus(LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (endDate.isBefore(today)) {
            return SubscriptionStatus.EXPIRED;
        } else {
            return SubscriptionStatus.ACTIVE;
        }
    }

    public SubscriptionResponseDTO updateSubscriptionStatus(String token, UUID subscriptionId, SubscriptionStatus newStatus) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }

        subscription.setStatus(newStatus);
        Subscription updated = subscriptionRepository.save(subscription);
        return mapToDTO(updated);
    }


    // ✅ Полностью заменить теги
    public SubscriptionResponseDTO replaceSubscriptionTags(String token, UUID subscriptionId, List<String> tags) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription subscription = getSubscriptionByIdAndUser(subscriptionId, userId);

        subscription.setTags(tags); // Заменяем список тегов
        Subscription updated = subscriptionRepository.save(subscription);
        return mapToDTO(updated);
    }

    // ✅ Добавить теги, не удаляя существующие
    public SubscriptionResponseDTO addSubscriptionTags(String token, UUID subscriptionId, List<String> newTags) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription subscription = getSubscriptionByIdAndUser(subscriptionId, userId);

        // Добавляем новые теги, не дублируя старые
        subscription.getTags().addAll(newTags.stream()
                .filter(tag -> !subscription.getTags().contains(tag))
                .toList());

        Subscription updated = subscriptionRepository.save(subscription);
        return mapToDTO(updated);
    }

    // ✅ Удалить определённые теги
    public SubscriptionResponseDTO removeSubscriptionTags(String token, UUID subscriptionId, List<String> tagsToRemove) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription subscription = getSubscriptionByIdAndUser(subscriptionId, userId);

        subscription.getTags().removeAll(tagsToRemove);
        Subscription updated = subscriptionRepository.save(subscription);
        return mapToDTO(updated);
    }

    // ✅ Получить месячные траты пользователя
    public BigDecimal getMonthlyExpenses(String token) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        return subscriptionRepository.getMonthlyExpenses(userId);
    }

    // ✅ Получить годовые траты пользователя
    public BigDecimal getYearlyExpenses(String token) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        return subscriptionRepository.getYearlyExpenses(userId);
    }

    // ✅ Найти самую дорогую подписку пользователя
    public Optional<SubscriptionResponseDTO> getMostExpensiveSubscription(String token) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        return subscriptionRepository.findMostExpensiveSubscription(userId).map(this::mapToDTO);
    }

    // ✅ Найти самую дешёвую подписку пользователя
    public Optional<SubscriptionResponseDTO> getCheapestSubscription(String token) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        return subscriptionRepository.findCheapestSubscription(userId).map(this::mapToDTO);
    }

    // ✅ Найти подписки, истекающие в ближайшие N дней
    public List<SubscriptionResponseDTO> getSubscriptionsExpiringSoon(String token, int days) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        LocalDate futureDate = LocalDate.now().plusDays(days);
        return subscriptionRepository.findSubscriptionsExpiringSoon(userId, futureDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🔥 Вспомогательный метод для поиска подписки
    private Subscription getSubscriptionByIdAndUser(UUID subscriptionId, UUID userId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
        return subscription;
    }


}
