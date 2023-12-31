package com.example.listmanager.configModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface BaseController<T> {
    ResponseEntity<?> create(@RequestBody T body);

    ResponseEntity<?> findAll();

    ResponseEntity<?> findById(@PathVariable UUID id);

    ResponseEntity<?> update(@RequestBody T body);
}
