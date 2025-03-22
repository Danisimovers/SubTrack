package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.project.subtrack.models.Subscription;

import java.math.BigDecimal;
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

    // (по желанию) Найти по сервису и пользователю (если вдруг понадобится)
    Optional<Subscription> findByServiceNameAndUserId(String serviceName, UUID userId);

    // (по желанию) Удалить только если подписка принадлежит пользователю
    void deleteByIdAndUserId(UUID subscriptionId, UUID userId);
    // Общие траты за указанный месяц
    // 1️⃣ Общие месячные траты пользователя
    @Query("SELECT COALESCE(SUM(s.price), 0) FROM Subscription s " +
            "WHERE s.user.id = :userId AND MONTH(s.endDate) = MONTH(CURRENT_DATE) AND YEAR(s.endDate) = YEAR(CURRENT_DATE)")
    BigDecimal getMonthlyExpenses(@Param("userId") UUID userId);

    // 2️⃣ Общие годовые траты пользователя
    @Query("SELECT COALESCE(SUM(s.price), 0) FROM Subscription s " +
            "WHERE s.user.id = :userId AND YEAR(s.endDate) = YEAR(CURRENT_DATE)")
    BigDecimal getYearlyExpenses(@Param("userId") UUID userId);

    // 3️⃣ Самая дорогая подписка пользователя
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId ORDER BY s.price DESC LIMIT 1")
    Optional<Subscription> findMostExpensiveSubscription(@Param("userId") UUID userId);

    // 4️⃣ Самая дешёвая подписка пользователя
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId ORDER BY s.price ASC LIMIT 1")
    Optional<Subscription> findCheapestSubscription(@Param("userId") UUID userId);

    // 5️⃣ Подписки, истекающие в ближайшие N дней
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.endDate BETWEEN CURRENT_DATE AND :futureDate")
    List<Subscription> findSubscriptionsExpiringSoon(@Param("userId") UUID userId, @Param("futureDate") LocalDate futureDate);

    // 6️⃣ Анализ расходов за последние N месяцев
    @Query("SELECT s.endDate, SUM(s.price) " +
            "FROM Subscription s WHERE s.user.id = :userId " +
            "AND s.endDate >= :startDate GROUP BY s.endDate ORDER BY s.endDate DESC")
    List<Object[]> getSpendingTrends(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate);


    // 7️⃣ Средняя стоимость подписки
    @Query("SELECT COALESCE(AVG(s.price), 0) FROM Subscription s WHERE s.user.id = :userId")
    BigDecimal getAverageSubscriptionCost(@Param("userId") UUID userId);

    // 8️⃣ Расходы по категориям
    @Query("SELECT s.category, SUM(s.price) FROM Subscription s " +
            "WHERE s.user.id = :userId GROUP BY s.category")
    List<Object[]> getExpensesByCategory(@Param("userId") UUID userId);

    // 9️⃣ Количество подписок по статусу
    @Query("SELECT s.status, COUNT(s) FROM Subscription s WHERE s.user.id = :userId GROUP BY s.status")
    List<Object[]> getSubscriptionsByStatus(@Param("userId") UUID userId);

}
