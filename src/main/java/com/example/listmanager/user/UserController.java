package com.example.listmanager.user;

import com.example.listmanager.configModel.BaseController;
import com.example.listmanager.contact.ContactDto;
import com.example.listmanager.jwt.JwtService;
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

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController implements BaseController<UserDto> {
    private final UserService userService;
    private final JwtService jwtService;
    private Bucket bucket;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, JwtService jwtService, Bucket bucket) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.bucket = bucket;
    }

    private void tryConsume() {
        if(!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @PostMapping(value="/register", headers="X-API-Version=1")
    public ResponseEntity<?> create(@RequestBody UserDto userDto) {
        try {
            tryConsume();

            logger.info("Request to User Registration API at time: " + Date.from(Instant.now()));

            logger.debug("List-manager-registration-service, class: " + this.getClass().getName()
                    + " with request data: " + userDto + " at time: " + Date.from(Instant.now())
            );

            ServiceResult result = userService.create(userDto);
            ResponseHandler<UserDto> response = new ResponseHandler<>();

            logger.debug("List-manager-registration-service, class: " + this.getClass().getName()
                    + " with response data: " + result + " at time: " + Date.from(Instant.now())
            );

            if (result.getStatus().is2xxSuccessful()) {
                String token = jwtService.generateToken(userDto.getUsername());
                return response.handleResponse(result, token);
            }

            logger.debug("List-manager-registration-service, class: " + this.getClass().getName()
                    + " with response data: " + result + " at time: " + Date.from(Instant.now())
            );

            logger.info("Response from Registration API at time: " + Date.from(Instant.now()));

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

    @PostMapping(value="/login", headers="X-API-Version=1")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            ServiceResult result = userService.login(userDto);
            ResponseHandler<UserDto> response = new ResponseHandler<>();
            if (result.getStatus().is2xxSuccessful()) {
                String token = jwtService.generateToken(userDto.getUsername());
                return response.handleResponse(result, token);
            }
            return response.handleResponse(result);
        } catch (DataAccessException e) {
            return new ResponseEntity<>("Database access error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.debug("Something went wrong with message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @Override
    @GetMapping(value="/all", headers="X-API-Version=1")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> findAll() {
        return new ResponseHandler<>().handleResponse(new ServiceResult<>("findAll method"));
    }

    /**
     * Endpoint to get sorted managers list
     * @param pageNumber
     * @param pageSize
     * @param sortProperty
     * @return
     */
    @GetMapping(value="/all-managers/{pageNumber}/{pageSize}",  headers="X-API-Version=1")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getSortedManagers(@PathVariable Integer pageNumber, @PathVariable Integer pageSize,
                                               @RequestParam(required = false) String sortProperty) {
        try {
            ServiceResult result = this.userService.getManagers(pageNumber, pageSize, sortProperty);
            ResponseHandler response = new ResponseHandler<>();
            return response.handleResponse(result);
        } catch (Exception e) {
            logger.debug("Something went wrong with message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @PostMapping("/find")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> findById(UUID id) {
        return null;
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority()")
    public ResponseEntity<?> update(UserDto dto) {
        return null;
    }

}
