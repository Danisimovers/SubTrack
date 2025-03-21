package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.subtrack.dto.SubscriptionDTO;
import ru.project.subtrack.dto.SubscriptionResponseDTO;
import ru.project.subtrack.exceptions.BusinessException;
import ru.project.subtrack.models.Subscription;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.SubscriptionRepository;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService; // Добавляем сервис отправки email

    // ✅ Получить все подписки текущего пользователя
    public List<SubscriptionResponseDTO> getUserSubscriptions(String token) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✅ Создать подписку для текущего пользователя
    public SubscriptionResponseDTO createSubscription(String token, SubscriptionDTO subscriptionData) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Проверяем, нет ли уже подписки на этот сервис
        if (subscriptionRepository.findByServiceNameAndUserId(subscriptionData.getServiceName(), userId).isPresent()) {
            throw new BusinessException("Subscription to this service already exists");
        }

        // Проверяем корректность дат
        if (subscriptionData.getEndDate().isBefore(subscriptionData.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        // Проверяем цену (должна быть положительной)
        if (subscriptionData.getPrice() == null || subscriptionData.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }

        // Сохраняем подписку
        Subscription subscription = Subscription.builder()
                .serviceName(subscriptionData.getServiceName())
                .startDate(subscriptionData.getStartDate())
                .endDate(subscriptionData.getEndDate())
                .price(subscriptionData.getPrice())
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

        // Проверка прав пользователя
        if (!existing.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }

        // Проверка дат
        if (updatedData.getEndDate().isBefore(updatedData.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        // Проверка цены
        if (updatedData.getPrice() == null || updatedData.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }


        // Обновляем поля
        existing.setServiceName(updatedData.getServiceName());
        existing.setStartDate(updatedData.getStartDate());
        existing.setEndDate(updatedData.getEndDate());
        existing.setPrice(updatedData.getPrice());

        Subscription updated = subscriptionRepository.save(existing);
        return mapToDTO(updated);
    }

    // ✅ Удалить подписку
    public void deleteSubscription(String token, UUID subscriptionId) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription existing = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        // Проверка прав
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
        dto.setPrice(subscription.getPrice()); // Добавляем цену
        return dto;
    }

    @Scheduled(cron = "0 22 21 * * *")
    @Transactional
    public void checkExpiringSubscriptions() {
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        List<Subscription> expiringSubscriptions = subscriptionRepository.findByEndDate(threeDaysLater);

        for (Subscription subscription : expiringSubscriptions) {
            String email = subscription.getUser().getEmail();
            System.out.println(email);
            String subject = "Ваша подписка скоро истекает";
            String message = "Здравствуйте, " + subscription.getUser().getName() +
                    "\n\nВаша подписка на " + subscription.getServiceName() +
                    " истекает через 3 дня (" + subscription.getEndDate() + ")." +
                    "\nПожалуйста, продлите её, если хотите продолжить пользоваться сервисом.";

            emailService.sendEmail(email, subject, message);
        }
    }
}
