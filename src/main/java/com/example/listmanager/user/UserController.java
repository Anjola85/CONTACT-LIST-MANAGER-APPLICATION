package com.example.listmanager.user;

import com.example.listmanager.ConfigModel.BaseController;
import com.example.listmanager.contact.ContactDto;
import com.example.listmanager.util.dto.ServiceResult;
import com.example.listmanager.util.helper.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController implements BaseController<UserDto> {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> create(@RequestBody UserDto userDto) {
        try {
            ServiceResult result = userService.create(userDto);
            ResponseHandler<UserDto> response = new ResponseHandler<>();
            if (result.getStatus().is2xxSuccessful()) {
                //TODO: generate JWT and add to response
                return response.handleResponse(result, "exmplaeToken");
            }
            return response.handleResponse(result);
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            ServiceResult result = userService.login(userDto);
            ResponseHandler<UserDto> response = new ResponseHandler<>();
            if (result.getStatus().is2xxSuccessful()) {
                //TODO: generate JWT and add to response
                return response.handleResponse(result, "exmplaeToken");
            }
            return response.handleResponse(result);
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
    public ResponseEntity<?> update(UserDto dto) {
        return null;
    }

}
