package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.project.subtrack.models.Subscription;
import ru.project.subtrack.models.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s JOIN s.tags t WHERE t.name = :tagName AND s.user.id = :userId")
    List<Subscription> findByTag(@Param("userId") UUID userId, @Param("tagName") String tagName);

    @Query("SELECT s FROM Subscription s JOIN s.tags t WHERE s.user.id = :userId AND t.name IN :tagNames")
    List<Subscription> findByTags(@Param("userId") UUID userId, @Param("tagNames") List<String> tagNames);

    @Query("SELECT s FROM Subscription s JOIN s.tags t WHERE s.user.id = :userId " +
            "AND t.name IN :tagNames " +
            "AND s.status = :status " +
            "AND s.endDate <= :endDate")
    List<Subscription> findByTagsAndStatusAndEndDateBefore(@Param("userId") UUID userId,
                                                           @Param("tagNames") List<String> tagNames,
                                                           @Param("status") SubscriptionStatus status,
                                                           @Param("endDate") LocalDate endDate);


    Optional<Subscription> findById(UUID subscriptionId);
    List<Subscription> findByUserId(UUID userId);


    List<Subscription> findByEndDate(LocalDate endDate);

    // (по желанию) Найти по сервису и пользователю (если вдруг понадобится)
    Optional<Subscription> findByServiceNameAndUserId(String serviceName, UUID userId);

    // Расходы по тегам
    @Query("SELECT t.name, SUM(s.price) FROM Subscription s " +
            "JOIN s.tags t WHERE s.user.id = :userId GROUP BY t.name")
    List<Object[]> getExpensesByTags(@Param("userId") UUID userId);

    // Количество подписок пользователя
    long countByUserId(@Param("userId") UUID userId);

    // Количество подписок по статусу
    @Query("SELECT s.status, COUNT(s) FROM Subscription s WHERE s.user.id = :userId GROUP BY s.status")
    List<Object[]> countByStatus(@Param("userId") UUID userId);


    // Средняя цена подписки
    @Query("SELECT AVG(s.price) FROM Subscription s WHERE s.user.id = :userId")
    BigDecimal getAverageSubscriptionPrice(@Param("userId") UUID userId);



    // (по желанию) Удалить только если подписка принадлежит пользователю
    void deleteByIdAndUserId(UUID subscriptionId, UUID userId);
    // Общие траты за указанный месяц
    // 1️⃣ Общие месячные траты пользователя
    @Query("SELECT COALESCE(SUM(s.price / \n" +
            "   CASE \n" +
            "       WHEN EXTRACT(YEAR FROM s.endDate) > EXTRACT(YEAR FROM s.startDate) THEN 12 \n" +
            "       ELSE 1 \n" +
            "   END), 0)\n" +
            "FROM Subscription s\n")
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





}
