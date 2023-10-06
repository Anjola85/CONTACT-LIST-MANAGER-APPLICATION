package com.example.listmanager.contact;

import com.example.listmanager.ConfigModel.BaseController;
import com.example.listmanager.util.dto.ServiceResult;
import com.example.listmanager.util.helper.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/addContact")
public class ContactController implements BaseController<ContactDto> {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public ResponseEntity<?> create(@RequestBody ContactDto body) {
        try {
            // TODO: remember to validate jwt and check if user is admin or manager - this should allow any though
            ServiceResult result = contactService.create(body);
            ResponseHandler<ContactDto> resp = new ResponseHandler<>();
            return resp.handleResponse(result);
        } catch (DataAccessException e) {
            return new ResponseEntity<>("Database access error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @Override
    public ResponseEntity<?> findAll() {
        return null;
    }

    @Override
    public ResponseEntity<?> findById(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(UUID id, ContactDto dto) {
        return null;
    }

}
