package com.example.listmanager.note;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class NoteDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty
    private String contactId;
    @JsonProperty("noteText")
    private String noteText;
    @JsonProperty("dateUpdated")
    private String dateUpdated;
    @JsonProperty("dateCreated")
    private String dateCreated;
}
