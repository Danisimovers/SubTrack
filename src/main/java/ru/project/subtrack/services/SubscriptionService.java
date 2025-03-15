package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.subtrack.dto.SubscriptionDTO;
import ru.project.subtrack.dto.SubscriptionResponseDTO;
import ru.project.subtrack.models.Subscription;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.SubscriptionRepository;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Получить все подписки текущего пользователя
    public List<SubscriptionResponseDTO> getUserSubscriptions(String token) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Создать подписку для текущего пользователя
    public SubscriptionResponseDTO createSubscription(String token, SubscriptionDTO subscriptionData) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Subscription subscription = new Subscription();
        subscription.setServiceName(subscriptionData.getServiceName());
        subscription.setStartDate(subscriptionData.getStartDate());
        subscription.setEndDate(subscriptionData.getEndDate());
        subscription.setUser(user);

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToDTO(saved);
    }

    // Обновить подписку
    public SubscriptionResponseDTO updateSubscription(String token, UUID subscriptionId, SubscriptionDTO updatedData) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        existing.setServiceName(updatedData.getServiceName());
        existing.setStartDate(updatedData.getStartDate());
        existing.setEndDate(updatedData.getEndDate());

        Subscription updated = subscriptionRepository.save(existing);
        return mapToDTO(updated);
    }

    // Удалить подписку
    public void deleteSubscription(String token, UUID subscriptionId) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        subscriptionRepository.delete(existing);
    }

    // Маппер в DTO
    private SubscriptionResponseDTO mapToDTO(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setServiceName(subscription.getServiceName());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setUserId(subscription.getUser().getId());
        dto.setUserEmail(subscription.getUser().getEmail());
        return dto;
    }
}
