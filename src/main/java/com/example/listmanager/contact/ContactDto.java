package com.example.listmanager.contact;
import com.example.listmanager.note.Note;
import com.example.listmanager.note.NoteDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ContactDto {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("address")
    private String address;

    @JsonProperty("notes")
    private List<NoteDto> notes = null;

    @JsonProperty("dateUpdated")
    private String dateUpdated;
    @JsonProperty("dateCreated")
    private String dateCreated;
}
