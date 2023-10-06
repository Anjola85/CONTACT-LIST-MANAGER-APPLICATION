package com.example.listmanager.note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    Optional<Note> findByContactId(UUID contactId);
}
