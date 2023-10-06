package com.example.listmanager.note;

import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Mapper Class
 */
@Configuration
public class NoteProcessor {
    public Note mapNoteInfoToEntity(NoteDto noteDto) {
        Note note = new Note();
        note.setContactId(UUID.fromString(noteDto.getContactId()));
        note.setNoteText(noteDto.getNoteText());
        return note;
    }

    public NoteDto mapNoteInfoToDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setContactId(note.getContactId().toString());
        dto.setNoteText(note.getNoteText());
        dto.setDateCreated(note.getDateCreated());
        dto.setDateUpdated(note.getDateUpdated());
        return dto;
    }
}
