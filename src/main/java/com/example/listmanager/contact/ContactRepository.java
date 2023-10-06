package com.example.listmanager.contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByLastName(String lastName, Pageable pageable);
    Page<Contact> findByFirstName(String address, Pageable pageable);
    Optional<Contact> findByEmail(String email);
    Optional<Contact> findByPhoneNumber(String phoneNumber);
    Page<Contact> findByAddress(String address, Pageable pageable);
    Page<Contact> getContactsByFirstName(String first_name, Pageable pageable);
    Page<Contact> getContactsByLastName(String last_name, Pageable pageable);
}
