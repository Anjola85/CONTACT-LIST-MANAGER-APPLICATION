package com.example.listmanager.contact;

import com.example.listmanager.ConfigModel.BaseService;
import com.example.listmanager.util.dto.ServiceResult;

import java.util.UUID;

public class ContactService implements BaseService<ContactDto> {
    @Override
    public ServiceResult<ContactDto> create(ContactDto dto) {
        // cannot create contact with same phone or email

        // create the contact for that user, create a note also if a note was added
        return null;
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

}
