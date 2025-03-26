package ru.project.subtrack.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // Название тега

    @ManyToMany(mappedBy = "tags")
    private Set<Subscription> subscriptions = new HashSet<>(); // Подписки, связанные с тегом
}
