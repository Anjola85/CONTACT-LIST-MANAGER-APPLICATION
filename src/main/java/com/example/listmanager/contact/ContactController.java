package com.example.listmanager.contact;

import com.example.listmanager.configModel.BaseController;
import com.example.listmanager.util.dto.ServiceResult;
import com.example.listmanager.util.helper.ResponseHandler;
import io.github.bucket4j.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/contact")
public class ContactController implements BaseController<ContactDto> {
    private final ContactService contactService;
    private final Bucket bucket;
    private final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    public ContactController(ContactService contactService, Bucket bucket) {
        this.contactService = contactService;
        this.bucket = bucket;
    }

    private void tryConsume() {
        if(!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @PostMapping(value="/add", headers="X-API-Version=1")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> create(@RequestBody ContactDto body) {
        try {
            tryConsume();

            ServiceResult result = contactService.create(body);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (DataAccessException e) {
            String message = "Database error with message: " + e.getMessage();
            ServiceResult result = new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (Exception e) {
            String message = "Something went wrong with message: " + e.getMessage();
            ServiceResult result = new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        }
    }

    /**
     *
     * @param userId - this the userId(managerID) that added all the contacts
     * @param pageNumber
     * @param pageSize
     * @param sortProperty
     * @param order - takes asc or dsc : specifies whether to sort in ascending or descending order
     * @return
     */
    @GetMapping(value="/view-all/{userId}/{pageNumber}/{pageSize}", headers="X-API-Version=1")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> getContacts(
            @PathVariable String userId,
            @PathVariable Integer pageNumber,
            @PathVariable Integer pageSize,
            @RequestParam(required = false, defaultValue = "firstName") String sortProperty,
            @RequestParam(required = false) String order
    ) {
        try {
            tryConsume();

            ServiceResult result = this.contactService.findContactByUserId(
                    UUID.fromString(userId), pageNumber, pageSize,
                    sortProperty, order
            );
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (DataAccessException e) {
            String message = "Database error with message: " + e.getMessage();
            ServiceResult result = new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (Exception e) {
            String message = "Something went wrong with message: " + e.getMessage();
            ServiceResult result = new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping(value="/update", headers="X-API-Version=1")
    public ResponseEntity<?> update(@RequestBody ContactDto body) {
        try {
            tryConsume();

            ServiceResult result = this.contactService.update(body);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (DataAccessException e) {
            String message = "Database error with message: " + e.getMessage();
            ServiceResult result = new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (Exception e) {
            String message = "Something went wrong with message: " + e.getMessage();
            ServiceResult result = new ServiceResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        }
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping(value="/delete", headers="X-API-Version=1")
    public ResponseEntity<?> deleteContact(@PathVariable String contactId) {
        try {
            UUID id = UUID.fromString(contactId);
            ServiceResult result = contactService.delete(id);

            if (result.getStatus().is2xxSuccessful()) {
                return ResponseEntity.ok("Contact deleted successfully");
            } else {
                return ResponseEntity.status(result.getStatus()).body(result.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error while deleting contact with ID: " + contactId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete contact");
        }
    }


    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> findAll() {
        return null;
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> findById(UUID id) {
        return null;
    }

}
