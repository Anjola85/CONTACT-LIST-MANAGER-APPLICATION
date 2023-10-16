package com.example.listmanager.contact;

import com.example.listmanager.note.NoteService;
import com.example.listmanager.util.dto.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactProcessor contactProcessor;

    @Mock
    private NoteService noteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testCreateWithIncompleteInput() {

        ContactDto dto = new ContactDto();
        dto.setUserId(UUID.randomUUID().toString());
        dto.setEmail("test@example.com");

        Contact contact = new Contact();
        contact.setUserId(UUID.fromString(dto.getUserId()));
        contact.setEmail(dto.getEmail());

        when(contactRepository.findContactByUserIdAndEmail(any(), any())).thenReturn(Optional.of(new ArrayList<Contact>() {{
            add(contact);
        }}));

        ServiceResult<ContactDto> result = contactService.create(dto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals("Address required", result.getMessage());
    }

    @Test
    public void testCreateWithValidInput() {
        UUID userId = UUID.randomUUID();

        ContactDto dto = new ContactDto();
        dto.setUserId(userId.toString());
        dto.setEmail("test@email.com");
        dto.setAddress("123 Test Street");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhoneNumber("1234567890");

        Contact mockContact = new Contact();
        mockContact.setEmail(dto.getEmail());
        mockContact.setAddress(dto.getAddress());
        mockContact.setFirstName(dto.getFirstName());
        mockContact.setLastName(dto.getLastName());
        mockContact.setPhoneNumber(dto.getPhoneNumber());

        when(contactProcessor.mapContactInfoToEntity(dto)).thenReturn(mockContact);
        when(contactRepository.findContactByUserIdAndEmail(userId, "test@email.com")).thenReturn(Optional.of(new ArrayList<>()));
        when(contactRepository.findContactByUserIdAndPhoneNumber(userId, "1234567890")).thenReturn(Optional.of(new ArrayList<>()));
        when(contactRepository.save(any(Contact.class))).thenReturn(mockContact);

        ServiceResult<ContactDto> result = contactService.create(dto);

        assertEquals("Successfully added Contact", result.getMessage());
        assertEquals(HttpStatus.CREATED, result.getStatus());
    }

    // Test for the delete method
    @Test
    public void testDeleteWithNonExistentContact() {
        // Given
        UUID contactId = UUID.randomUUID();

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When
        ServiceResult<ContactDto> result = contactService.delete(contactId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        assertEquals("Contact not found", result.getMessage());
    }

    @Test
    public void testUpdateWithValidInput() {
        UUID userId = UUID.randomUUID();

        ContactDto dto = new ContactDto();
        dto.setId(userId.toString());
        dto.setUserId(userId.toString());
        dto.setEmail("test@email.com");
        dto.setAddress("123 Test Street");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhoneNumber("1234567890");

        Contact mockContact = new Contact();
        mockContact.setId(userId);
        mockContact.setEmail(dto.getEmail());
        mockContact.setAddress(dto.getAddress());
        mockContact.setFirstName(dto.getFirstName());
        mockContact.setLastName(dto.getLastName());
        mockContact.setPhoneNumber(dto.getPhoneNumber());

        when(contactProcessor.mapContactInfoToEntity(dto)).thenReturn(mockContact);
        when(contactRepository.findContactByUserIdAndId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(mockContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(mockContact);

        ServiceResult<ContactDto> result = contactService.update(dto);

        assertEquals("Successfully updated Contact", result.getMessage());
        assertEquals(HttpStatus.OK, result.getStatus());
    }

    @Test
    public void testFindContactByUserIdNoContacts() throws Exception {
        UUID userId = UUID.randomUUID();
        Page<Contact> contactPage = Page.empty();
        when(contactRepository.findContactsByUserId(eq(userId), any(Pageable.class))).thenReturn(contactPage);

        ServiceResult<List<ContactDto>> result = contactService.findContactByUserId(userId, 1, 10, "email", "asc");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Contact is empty", result.getMessage());
        assertTrue(!result.getData().isEmpty());
    }

}
