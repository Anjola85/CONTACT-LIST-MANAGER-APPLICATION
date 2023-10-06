package com.example.listmanager.contact;

import com.example.listmanager.ConfigModel.BaseService;
import com.example.listmanager.note.Note;
import com.example.listmanager.note.NoteDto;
import com.example.listmanager.note.NoteService;
import com.example.listmanager.util.dto.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactService implements BaseService<ContactDto> {
    private ContactRepository contactRepository;
    private ContactProcessor contactProcessor;
    private NoteService noteService;

    @Autowired
    ContactService(ContactRepository contactRepository, ContactProcessor contactProcessor, NoteService noteService) {
        this.contactRepository = contactRepository;
        this.contactProcessor = contactProcessor;
        this.noteService = noteService;
    }

    @Override
    public ServiceResult<ContactDto> create(ContactDto dto) {
        ServiceResult BAD_REQUEST = validateInput(dto);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        Contact contact = contactProcessor.mapContactInfoToEntity(dto);
        ContactDto resp;

        // cannot create contact with same phone or email, check if they exist already
        Optional<List<Contact>> entityResp = contactRepository.findContactByUserIdAndEmail(contact.getUserId(), contact.getEmail());
        // check again with phone number
        if(entityResp.isEmpty())
            entityResp = contactRepository.findContactByUserIdAndPhoneNumber(contact.getUserId(), contact.getPhoneNumber(), Pageable.unpaged());

        // if exists, return the contact with error message
        if(entityResp.isPresent()) {
            resp = contactProcessor.mapContactInfoToDto(entityResp.get().get(0));
            return new ServiceResult(HttpStatus.CONFLICT, "Contact already exists", resp);
        }

        // create the contact for that user, create a note also if a note was added
        resp = contactProcessor.mapContactInfoToDto(contactRepository.save(contact));

        if(dto.getNote().getNoteText() != null) {
            dto.getNote().setContactId(resp.getId());
            NoteDto note = this.noteService.create(dto.getNote()).getData().get(0);
            resp.setNote(note);
        }

        return new ServiceResult(HttpStatus.CREATED, "Successfully added Contact", resp);
    }

    @Override
    public ServiceResult<ContactDto> findAll() {
        return null;
    }

    @Override
    public ServiceResult<ContactDto> findById(UUID id) {
        return null;
    }

    @Override
    public ServiceResult<ContactDto> update(UUID id, ContactDto dto) {
        return null;
    }

    // ADMIN role
    // get all contacts with same userId - might need pagination

    // get all contacts with same address

    // get all contacts with same firstName

    // get all contacts with same lastName

    // get all contacts with same phoneNumber

    // get all contacts with same email

    // all operation the manager can perform should be within a userId



    // get all contacts with respect to UserId in order of FirstName

    // get all contacts with respect to UserId in order of LastName

    // get all contacts irrespective of userId in order of FirstName - alphabetically

    // get all contacts  irrespective of userId in order of lastName - alphabetically



    private static ServiceResult validateInput(ContactDto dto) {
        if(dto.getUserId() == null || dto.getUserId().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "UserId required");
        if(dto.getAddress() == null || dto.getAddress().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Address required");
        if(dto.getEmail() == null || dto.getEmail().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Email required");
        if(dto.getFirstName() == null || dto.getFirstName().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Firstname required");
        if(dto.getLastName() == null || dto.getLastName().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Lastname required");
        if(dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Phone number required");
        return null;
    }
}
