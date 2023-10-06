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
    // admin role
    Page<Contact> findByAddress(String address, Pageable pageable);
    Page<Contact> findByFirstName(String firsName, Pageable pageable);
    Page<Contact> findByLastName(String lastName, Pageable pageable);
    Page<Contact> findByPhoneNumber(String phoneNumber, Pageable pageable);
    Page<Contact> findByEmail(String email, Pageable pageable);
    Page<Contact> findContactsByDateCreated(String date, Pageable pageable);
    Page<Contact> findContactsByDateUpdated(String date, Pageable pageable);
    Page<Contact> findContactsByDateCreatedAfter(String date, Pageable pageable);
    Page<Contact> findContactByDateUpdatedAfter(String date, Pageable pageable);
    Page<Contact> findContactsByDateCreatedBefore(String date, Pageable pageable);
    Page<Contact> findContactByDateUpdatedBefore(String date, Pageable pageable);

    // specific to a user
    Page<Contact> findContactByUserId(UUID userId, Pageable pageable);
    Optional<List<Contact>> findContactByUserIdAndPhoneNumber(UUID userId, String phoneNumber);
    Optional<List<Contact>> findContactByUserIdAndEmail(UUID userId, String email);

    // many contacts can have same address
    Page<Contact> findContactsByUserIdAndAddress(UUID userId, String address, Pageable pageable);

    // many contacts can have same firstname
    Page<Contact> findContactsByUserIdAndFirstName(UUID userId, String firsName, Pageable pageable);

    // many contacts can have same lastname
    Page<Contact> findContactsByUserIdAndLastName(UUID userId, String lastName, Pageable pageable);


    Page<Contact> findContactsByUserIdAndDateUpdated(String date, UUID userId, Pageable pageable);

    Page<Contact> findContactsByUserIdAndDateCreatedAfter(String date, UUID userId, Pageable pageable);

    Page<Contact> findContactsByUserIdAndDateUpdatedAfter(String date, UUID userId, Pageable pageable);

    Page<Contact> findContactsByUserIdAndDateCreatedBefore(String date, UUID userId, Pageable pageable);

    Page<Contact> findContactsByUserIdAndDateUpdatedBefore(String date, UUID userId, Pageable pageable);

}
