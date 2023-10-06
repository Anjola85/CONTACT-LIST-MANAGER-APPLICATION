package com.example.listmanager.contact;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ContactProcessor {
    public Contact mapContactInfoToEntity(ContactDto dto) {
        Contact entity = new Contact();
        // map note if added
        return entity;
    }

    public ContactDto mapContactInfoToDto(Contact entity) {
        ContactDto dto = new ContactDto();

        return dto;
    }
}
