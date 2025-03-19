package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.project.subtrack.models.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Поиск по email
    Optional<User> findByEmail(String email);

    // Поиск по телефону
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Поиск по username (если username отдельное поле)
    Optional<User> findByName(String name);

    // Поиск для логина (email или phone)
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phoneNumber = :identifier")
    Optional<User> findByEmailOrPhoneNumber(@Param("identifier") String identifier);


    // Проверки на существование
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByName(String name); // для username

}
