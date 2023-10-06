package com.example.listmanager.note;

import com.example.listmanager.ConfigModel.BaseService;
import com.example.listmanager.util.dto.ServiceResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteService implements BaseService<NoteDto> {
    private NoteRepository noteRepository;
    private NoteProcessor noteProcessor;
    private ServiceResult<NoteDto> response;

    @Autowired
    public NoteService(NoteRepository noteRepository,
                       NoteProcessor noteProcessor,
                       ServiceResult response) {
        this.noteRepository = noteRepository;
        this.noteProcessor = noteProcessor;
        this.response = response;
    }

    @Override
    public ServiceResult<NoteDto> create(NoteDto dto) {
        ServiceResult BAD_REQUEST = validateInput(dto);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        Note note = noteProcessor.mapNoteInfoToEntity(dto);

        // check if a note already exists for the contactId, if yes, just update the note
        Optional<Note> resp = noteRepository.findByContactId(note.getContactId());

        if(resp.isPresent()) {
            Note noteEntity = resp.get();

            note.setId(noteEntity.getId());
            // update the note text
            BeanUtils.copyProperties(note, noteEntity);
        }
        NoteDto savedNote = noteProcessor.mapNoteInfoToDto(noteRepository.save(note));
        return new ServiceResult<>(HttpStatus.CREATED, "Note added successfully", savedNote);
    }

    public ServiceResult<NoteDto> findByContactId(String id) {
        UUID contactId = UUID.fromString(id);
        Optional<Note> note = this.noteRepository.findByContactId(contactId);
        if(note.isPresent()) {
            NoteDto noteDto = noteProcessor.mapNoteInfoToDto(note.get());
            return new ServiceResult(HttpStatus.OK, "Found Note", noteDto);
        }
        return new ServiceResult(HttpStatus.NOT_FOUND, "Note does not exist");
    }

    @Override
    public ServiceResult<NoteDto> findAll() {
        List<NoteDto> notes = noteRepository.findAll().stream()
                .map(user -> noteProcessor.mapNoteInfoToDto(user))
                .collect(Collectors.toList());
        return new ServiceResult(HttpStatus.OK, "Notes successfully fetched", notes);
    }

    @Override
    public ServiceResult<NoteDto> findById(UUID id) {
        Optional<Note> note = noteRepository.findById(id);
        if(note.isEmpty()) {
            return new ServiceResult(HttpStatus.NOT_FOUND,"user does not exist");
        }
        Note noteEntity = note.get();
        NoteDto noteResponse = noteProcessor.mapNoteInfoToDto(noteEntity);
        return new ServiceResult(HttpStatus.OK, "user successfully fetched", noteResponse);
    }

    @Override
    public ServiceResult<NoteDto> update(UUID id, NoteDto dto) {
        return this.create(dto);
    }

    private static ServiceResult validateInput(NoteDto dto) {
        // validate input
        if(dto.getContactId() == null || dto.getContactId().toString().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "ContactId is required");
        if(dto.getNoteText() == null || dto.getNoteText().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Note cannot be empty");
        return null;
    }
}
