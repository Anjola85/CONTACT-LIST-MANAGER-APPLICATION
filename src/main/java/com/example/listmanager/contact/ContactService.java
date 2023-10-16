package com.example.listmanager.contact;

import com.example.listmanager.configModel.BaseService;
import com.example.listmanager.note.NoteDto;
import com.example.listmanager.note.NoteService;
import com.example.listmanager.util.dto.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    ContactService(ContactRepository contactRepository, ContactProcessor contactProcessor, NoteService noteService) {
        this.contactRepository = contactRepository;
        this.contactProcessor = contactProcessor;
        this.noteService = noteService;
    }

    @Override
    public ServiceResult<ContactDto> create(ContactDto dto) {
        try {
            ServiceResult BAD_REQUEST = validateInput(dto);
            if (BAD_REQUEST != null) return BAD_REQUEST;

            Contact contact = contactProcessor.mapContactInfoToEntity(dto);

            ContactDto resp;

            // cannot create contact with same phone or email, check if they exist already
            Optional<List<Contact>> entityResp = contactRepository.findContactByUserIdAndEmail(contact.getUserId(), contact.getEmail());
            // check again with phone number
            if(!entityResp.isPresent() || entityResp.get().isEmpty())
                entityResp = contactRepository.findContactByUserIdAndPhoneNumber(contact.getUserId(), contact.getPhoneNumber());

            // if exists, return the contact with error message
            if(!entityResp.isEmpty() && !entityResp.get().isEmpty())
                return new ServiceResult(HttpStatus.CONFLICT, "Contact already exists");


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
        catch (DataAccessException dae) {
            logger.debug(String.format("Database error with error message: %s", dae.getMessage()));
            throw dae;
        }
        catch(Exception e) {
            logger.debug(String.format("Erorr occured in %s with error %s", this.getClass().getName(), e));
            throw e;
        }
    }

    @Override
    public ServiceResult<ContactDto> update(ContactDto dto) {
        try {
            ServiceResult badRequest = validateInput(dto);
            if (badRequest != null || (dto.getId() == null || dto.getId().isEmpty())){
                if ((dto.getId() == null || dto.getId().isEmpty()))
                    return new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, "ContactId cannot be null");
                return badRequest;
            }

            // map
            Contact contactEntity = contactProcessor.mapContactInfoToEntity(dto);

            Optional<Contact> existingContact = contactRepository.findContactByUserIdAndId(UUID.fromString(dto.getUserId()), contactEntity.getId());

            if (existingContact.isEmpty())
                return new ServiceResult(HttpStatus.NOT_FOUND, "Unable to update, contact not found");

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
        catch (DataAccessException dae) {
            logger.debug(String.format("Database error with error message: %s", dae.getMessage()));
            throw dae;
        }
        catch(Exception e) {
            logger.debug(String.format("Erorr occured in %s with error %s", this.getClass().getName(), e));
            throw e;
        }

    }

    @Override
    public ServiceResult<ContactDto> delete(UUID contactId) {
        try{
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
        catch (DataAccessException dae) {
            logger.debug(String.format("Database error with error message: %s", dae.getMessage()));
            throw dae;
        }
        catch(Exception e) {
            logger.debug(String.format("Erorr occured in %s with error %s", this.getClass().getName(), e));
            throw e;
        }
    }

    public ServiceResult<List<ContactDto>> findContactByUserId(UUID id, Integer pageNumber, Integer pageSize, String sortProperty, String order) {
        try {
            Pageable pageable;

            if(pageNumber == null && pageSize == null && sortProperty == null)
                pageable = Pageable.unpaged();
            // get in ascending order of provided sortProperty by default
            else if(order == null || order.toLowerCase().equals("asc"))
                pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sortProperty);
            else
                pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, sortProperty);

            Page<Contact> contactPage = contactRepository.findContactsByUserId(id, pageable);

            if (contactPage.isEmpty())
                return new ServiceResult( HttpStatus.OK, "Contact is empty", contactPage.get());

            // Map each contact to ContactDto
            List<ContactDto> contactList = contactPage.getContent()
                    .stream()
                    .map(contact -> contactProcessor.mapContactInfoToDto(contact)) // Assuming ContactDtoMapper is your mapper class
                    .toList();

            CompletableFuture<List<ContactDto>> enrichedContactsFuture = enrichContactsWithNotesAsync(contactList);

            List<ContactDto> enrichedContacts = enrichedContactsFuture.get();

            return new ServiceResult(HttpStatus.OK, String.format("Contacts successfully retrieved with result: %d", enrichedContacts.size()), enrichedContacts);
        } catch (IllegalArgumentException e) {
            return new ServiceResult( HttpStatus.BAD_REQUEST, "Invalid parameter");
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

    @Override
    public ServiceResult<ContactDto> findAll() {
        return null;
    }

    @Override
    public ServiceResult<ContactDto> findById(UUID id) {
        return null;
    }



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
