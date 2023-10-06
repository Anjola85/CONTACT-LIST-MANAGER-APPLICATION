package com.example.listmanager.contact;

import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ContactProcessor {
    public Contact mapContactInfoToEntity(ContactDto dto) {
        Contact entity = new Contact();
        entity.setUserId(UUID.fromString(dto.getUserId()));
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return entity;
    }

    public ContactDto mapContactInfoToDto(Contact entity) {
        ContactDto dto = new ContactDto();
        dto.setId(entity.getId().toString());
        dto.setUserId(entity.getUserId().toString());
        dto.setAddress(entity.getAddress());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setDateCreated(entity.getDateCreated());
        dto.setDateUpdated(entity.getDateUpdated());
        return dto;
    }
}
