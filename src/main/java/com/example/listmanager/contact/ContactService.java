package com.example.listmanager.contact;

import com.example.listmanager.ConfigModel.BaseService;
import com.example.listmanager.note.Note;
import com.example.listmanager.note.NoteDto;
import com.example.listmanager.note.NoteService;
import com.example.listmanager.user.User;
import com.example.listmanager.user.UserService;
import com.example.listmanager.util.dto.ServiceResult;
import com.example.listmanager.util.helper.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ContactService implements BaseService<ContactDto> {
    private ContactRepository contactRepository;
    private ContactProcessor contactProcessor;
    private NoteService noteService;
    private UserService userService;

    @Autowired
    ContactService(ContactRepository contactRepository, ContactProcessor contactProcessor, NoteService noteService, UserService userService) {
        this.contactRepository = contactRepository;
        this.contactProcessor = contactProcessor;
        this.noteService = noteService;
        this.userService = userService;
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
        if(entityResp.get().isEmpty())
            entityResp = contactRepository.findContactByUserIdAndPhoneNumber(contact.getUserId(), contact.getPhoneNumber());

        // if exists, return the contact with error message
        if(!entityResp.get().isEmpty()) {
            resp = contactProcessor.mapContactInfoToDto(entityResp.get().get(0));
            return new ServiceResult(HttpStatus.CONFLICT, "Contact already exists");
        }

        // validate userId
        ServiceResult userResp = userService.findById(contact.getUserId());
        if(userResp.getStatus().isError() || userResp.getData() == null)
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Invalid user id");


        // create the contact for that user, create a note also if a note was added
        resp = contactProcessor.mapContactInfoToDto(contactRepository.save(contact));

        // create note for contact if it was added
        if(dto.getNote() != null && dto.getNote().getNoteText() != null) {
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
    public ServiceResult<ContactDto> update(ContactDto dto) {
        ServiceResult badRequest = validateInput(dto);
        if (badRequest != null)
            return badRequest;

        // map
        Contact contactEntity = contactProcessor.mapContactInfoToEntity(dto);

        Optional<Contact> existingContact = contactRepository.findById(contactEntity.getId());

        if (existingContact.isEmpty())
            return new ServiceResult(HttpStatus.NOT_FOUND, "Unable to update, contact not found");

        // Validate userId
        ServiceResult userResp = userService.findById(contactEntity.getUserId());
        if (userResp.getStatus().isError() || userResp.getData() == null) {
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Invalid user id");
        }

        Contact updatedContact = existingContact.get();
        ContactDto resp;

        // Check if the updated email already exists for another contact
        if (!dto.getEmail().equals(updatedContact.getEmail())) {
            Optional<List<Contact>> entityResp = contactRepository.findContactByUserIdAndEmail(updatedContact.getUserId(), dto.getEmail());
            if (!entityResp.get().isEmpty())
                return new ServiceResult(HttpStatus.CONFLICT, "Email already exists");
        }

        // Check if the updated phone number already exists for another contact
        if (!dto.getPhoneNumber().equals(updatedContact.getPhoneNumber())) {
            Optional<List<Contact>> entityResp = contactRepository.findContactByUserIdAndPhoneNumber(updatedContact.getUserId(), dto.getPhoneNumber());
            if (!entityResp.get().isEmpty()) {
                return new ServiceResult(HttpStatus.CONFLICT, "Phone number already exists");
            }
        }


        // Update the contact with the new data
        updatedContact.setFirstName(dto.getFirstName());
        updatedContact.setLastName(dto.getLastName());
        updatedContact.setEmail(dto.getEmail());
        updatedContact.setPhoneNumber(dto.getPhoneNumber());
        updatedContact.setAddress(dto.getAddress());

        // Save the updated contact
        resp = contactProcessor.mapContactInfoToDto(contactRepository.save(updatedContact));

        // Check if notes were provided and, if so, update the note
        if (dto.getNote() != null && dto.getNote().getNoteText() != null) {
            NoteDto noteDto = dto.getNote();
            noteDto.setContactId(updatedContact.getId().toString());
            ServiceResult<NoteDto> noteResult = noteService.create(noteDto);

            if (noteResult.getStatus().isError()) {
                return new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update note");
            }

            resp.setNote(noteResult.getData().get(0));
        }

        return new ServiceResult(HttpStatus.OK, "Successfully updated Contact", resp);

    }

    @Override
    public ServiceResult<ContactDto> delete(UUID contactId) {
        Optional<Contact> existingContact = contactRepository.findById(contactId);

        if (existingContact.isEmpty()) {
            return new ServiceResult(HttpStatus.NOT_FOUND, "Contact not found");
        }

        // Delete the associated note, if it exists
        ServiceResult<NoteDto> noteResp = noteService.findByContactId(contactId.toString());

        ServiceResult<NoteDto>  resp;
        if(noteResp.getStatus().is2xxSuccessful())
            resp = noteService.delete(UUID.fromString(noteResp.getData().get(0).getId()));

        // Delete the contact
        contactRepository.deleteById(contactId);

        return new ServiceResult(HttpStatus.OK, "Contact and associated note deleted successfully");

    }

    // ADMIN role
    // get all contacts with same userId - might need pagination

    // get all contacts with same address

    // get all contacts with same firstName

    // get all contacts with same lastName

    // get all contacts with same phoneNumber

    // get all contacts with same email

    // all operation the manager can perform should be within a userId


    // MANAGER ROLE
    public ServiceResult<List<ContactDto>> findContactbyUserId(UUID id) {
        try {

            Page<Contact> contactPage = contactRepository.findContactsByUserId(id, Pageable.unpaged());

            if (contactPage.isEmpty())
                return new ServiceResult( HttpStatus.OK, "Contact is empty", contactPage.get());

            // Map each contact to ContactDto
            List<ContactDto> contactList = contactPage.getContent()
                    .stream()
                    .map(contact -> contactProcessor.mapContactInfoToDto(contact)) // Assuming ContactDtoMapper is your mapper class
                    .toList();

            CompletableFuture<List<ContactDto>> enrichedContactsFuture = enrichContactsWithNotesAsync(contactList);

            List<ContactDto> enrichedContacts = enrichedContactsFuture.get();

            return new ServiceResult(HttpStatus.OK, "Contacts successfully retrieved", enrichedContacts);
        } catch (IllegalArgumentException e) {
            return new ServiceResult( HttpStatus.BAD_REQUEST, "Invalid userId");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public CompletableFuture<List<ContactDto>> enrichContactsWithNotesAsync(List<ContactDto> dto) {
        List<CompletableFuture<ContactDto>> futures = dto.stream()
                .map(contact -> {
                    String contactId = contact.getId();

                    return CompletableFuture.supplyAsync(() -> {
                        ServiceResult<NoteDto> noteResult = noteService.findByContactId(contactId);

                        if (!noteResult.getStatus().isError()) {
                            contact.setNote(noteResult.getData().get(0));
                        }
                        return contact;
                    });
                })
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(ignored -> {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        });
    }

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
