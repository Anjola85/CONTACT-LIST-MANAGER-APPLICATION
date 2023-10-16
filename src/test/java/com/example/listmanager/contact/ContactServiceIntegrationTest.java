package com.example.listmanager.contact;

import com.example.listmanager.note.NoteService;
import com.example.listmanager.util.dto.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(ContactService.class)
public class ContactServiceIntegrationTest {

    @Autowired
    private ContactService contactService;

    @MockBean
    private NoteService noteService;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    public void setUp() {
        contactRepository.deleteAll();
    }

    @Test
    public void testCreateContactWithValidData() {
        ContactDto dto = new ContactDto();
        dto.setUserId(UUID.randomUUID().toString());
        dto.setEmail("test@email.com");
        dto.setAddress("Test Address");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhoneNumber("1234567890");

        ServiceResult<ContactDto> result = contactService.create(dto);

        assertEquals(HttpStatus.CREATED, result.getStatus());
        assertEquals("Successfully added Contact", result.getMessage());
    }


}
