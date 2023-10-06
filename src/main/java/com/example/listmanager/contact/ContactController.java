package com.example.listmanager.contact;

import com.example.listmanager.ConfigModel.BaseController;
import com.example.listmanager.util.dto.ServiceResult;
import com.example.listmanager.util.helper.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contact")
public class ContactController implements BaseController<ContactDto> {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody ContactDto body) {
        try {
            // TODO: remember to validate jwt and check if user is admin or manager - this should allow any req though
            // TODO: also should be pulling userId from JWT token attached
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

    @GetMapping("/view-all")
    public ResponseEntity<?> handleGetRequest(@RequestParam String userId) {
        try {
            //TODO: remeber to get userId from decrypted token
            ServiceResult result = this.contactService.findContactbyUserId(userId);
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
    public ResponseEntity<?> findAll() {
        return null;
    }

    @Override
    public ResponseEntity<?> findById(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(@RequestBody ContactDto body) {
        try {
            //TODO: remeber to get userId from decrypted token
            // set the userId from the JWT
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

}
