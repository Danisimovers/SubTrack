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
import ru.project.subtrack.models.SubscriptionStatus;
import ru.project.subtrack.models.Tag;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.SubscriptionRepository;
import ru.project.subtrack.repositories.TagRepository;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final TagRepository tagRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService; // Добавляем сервис отправки email

    // ✅ Получить все подписки текущего пользователя (с фильтрацией)
    public List<SubscriptionResponseDTO> getUserSubscriptions(String token, SubscriptionStatus status, List<String> tags) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);

        return subscriptions.stream()
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

        // Проверка наличия подписки на этот сервис
        if (subscriptionRepository.findByServiceNameAndUserId(subscriptionData.getServiceName(), userId).isPresent()) {
            throw new BusinessException("Subscription to this service already exists");
        }

        // Проверка корректности дат
        if (subscriptionData.getEndDate().isBefore(subscriptionData.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        // Проверка цены
        if (subscriptionData.getPrice() == null || subscriptionData.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }

        // Получаем теги из базы данных по их именам

        Set<Tag> tags = new HashSet<>();
        if (subscriptionData.getTags() != null && !subscriptionData.getTags().isEmpty()) {
            for (String tagName : subscriptionData.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseThrow(() -> new BusinessException("Tag not found: " + tagName));
                tags.add(tag);
            }
        }


        // Создаём подписку
        Subscription subscription = Subscription.builder()
                .serviceName(subscriptionData.getServiceName())
                .startDate(subscriptionData.getStartDate())
                .endDate(subscriptionData.getEndDate())
                .price(subscriptionData.getPrice())
                .tags(tags)  // Устанавливаем теги как сущности
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

        // Проверки для обновления
        if (updatedData.getEndDate().isBefore(updatedData.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        if (updatedData.getPrice() == null || updatedData.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }

        // Обновление тегов из базы данных
        Set<Tag> tags = new HashSet<>();
        if (updatedData.getTags() != null && !updatedData.getTags().isEmpty()) {
            for (String tagName : updatedData.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseThrow(() -> new BusinessException("Tag not found: " + tagName));
                tags.add(tag);
            }
        }

        // Обновление подписки
        existing.setServiceName(updatedData.getServiceName());
        existing.setStartDate(updatedData.getStartDate());
        existing.setEndDate(updatedData.getEndDate());
        existing.setPrice(updatedData.getPrice());
        existing.setTags(tags);
        existing.setStatus(calculateStatus(updatedData.getEndDate()));

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
        dto.setPrice(subscription.getPrice());
        dto.setStatus(subscription.getStatus());

        // Преобразование Set<Tag> в List<String>
        List<String> tagNames = subscription.getTags().stream()
                .map(Tag::getName)  // Извлекаем имя из каждого тега
                .collect(Collectors.toList());  // Собираем в список строк

        dto.setTags(tagNames);  // Устанавливаем список имен тегов

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


    // Метод для проверки существующих тегов
    private Set<Tag> validateAndGetTags(List<String> tagNames) {
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);

        if (existingTags.size() != tagNames.size()) {
            // Если хотя бы один тег не найден, выбрасываем исключение
            throw new IllegalArgumentException("Некоторые теги не существуют");
        }

        return new HashSet<>(existingTags);
    }

    // Замена тегов
    public SubscriptionResponseDTO replaceSubscriptionTags(String token, UUID subscriptionId, List<String> tags) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription subscription = getSubscriptionByIdAndUser(subscriptionId, userId);

        Set<Tag> validatedTags = validateAndGetTags(tags);
        subscription.setTags(validatedTags);

        Subscription updated = subscriptionRepository.save(subscription);
        return mapToDTO(updated);
    }

    // Добавление тегов
    public SubscriptionResponseDTO addSubscriptionTags(String token, UUID subscriptionId, List<String> newTags) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        Subscription subscription = getSubscriptionByIdAndUser(subscriptionId, userId);

        Set<Tag> validatedTags = validateAndGetTags(newTags);

        // Добавляем только новые теги
        subscription.getTags().addAll(validatedTags);

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
    public List<String> getAllTags() {
        return tagRepository.findAllTags(); // Нужно создать этот метод в `TagRepository`
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



    @Scheduled(cron = "0 34 14 * * *")
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
