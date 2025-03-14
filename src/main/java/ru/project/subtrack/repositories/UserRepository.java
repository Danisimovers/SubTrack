package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.project.subtrack.models.User;
import ru.project.subtrack.models.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    // Можно добавить поиск по телефону, если нужно
    Optional<User> findByPhoneNumber(String phoneNumber);
}
