package com.example.listmanager.contact;

import com.example.listmanager.ConfigModel.BaseController;
import org.springframework.http.ResponseEntity;

public class ContactController implements BaseController<Contact> {
    @Override
    public ResponseEntity<?> create(Contact body) {
        return null;
    }

    @Override
    public ResponseEntity<?> findAll() {
        return null;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(Long id, Contact dto) {
        return null;
    }
}
