package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.subtrack.models.Subscription;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.SubscriptionRepository;
import ru.project.subtrack.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service // Помечаем как сервисный класс
@RequiredArgsConstructor // Lombok создает конструктор для final полей
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    /**
     * Создание новой подписки для пользователя.
     * Проверяет, существует ли пользователь.
     */
    public Subscription createSubscription(Subscription subscription, UUID userId) {
        // Проверяем, есть ли пользователь
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь с таким ID не найден");
        }

        // Назначаем пользователя подписке
        subscription.setUser(userOptional.get());

        // Сохраняем подписку
        return subscriptionRepository.save(subscription);
    }

    /**
     * Получить все подписки для конкретного пользователя.
     */
    public List<Subscription> getSubscriptionsForUser(UUID userId) {
        // Проверяем, что пользователь существует
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь с таким ID не найден");
        }

        // Возвращаем список подписок
        return subscriptionRepository.findByUserId(userId);
    }

    /**
     * Удалить подписку по ID.
     * Если нет подписки с таким ID — ошибка.
     */
    public void deleteSubscription(UUID subscriptionId) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOptional.isEmpty()) {
            throw new RuntimeException("Подписка с таким ID не найдена");
        }
        subscriptionRepository.deleteById(subscriptionId);
    }

    /**
     * Редактировать подписку.
     * Например, изменить дату окончания или название сервиса.
     */
    public Subscription updateSubscription(UUID subscriptionId, Subscription updatedSubscription) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOptional.isEmpty()) {
            throw new RuntimeException("Подписка с таким ID не найдена");
        }

        Subscription existingSubscription = subscriptionOptional.get();

        // Обновляем поля (в зависимости от того, что передали)
        existingSubscription.setServiceName(updatedSubscription.getServiceName());
        existingSubscription.setStartDate(updatedSubscription.getStartDate());
        existingSubscription.setEndDate(updatedSubscription.getEndDate());

        // Сохраняем обновленную подписку
        return subscriptionRepository.save(existingSubscription);
    }

    /**
     * Получить подписку по ID.
     */
    public Optional<Subscription> findById(UUID id) {
        return subscriptionRepository.findById(id);
    }
}
