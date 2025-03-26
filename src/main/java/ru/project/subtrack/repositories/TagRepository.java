package ru.project.subtrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.project.subtrack.models.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
     Optional<Tag> findByName(String name);
     List<Tag> findByNameIn(List<String> names);
     @Query("SELECT DISTINCT t.name FROM Tag t")
     List<String> findAllTags();

}

