package com.example.listmanager.util.helper;

import com.example.listmanager.util.dto.ServiceResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * This class contains methods that help return formatted result to client
 */
public class ResponseHandler<T> {
    private JsonParser<T> json;

    public ResponseHandler() {
        json = new JsonParser<T>();
    }

    /**
     * Generic response
     * @param result
     * @return
     */
    public ResponseEntity<?> handleResponse(ServiceResult<T> result) {
        json.createResponse(result);
        return ResponseEntity.status(result.getStatus()).body(json);
    }

    /**
     * Response with token
     * @param result
     * @param jwtToken
     * @return
     */
    public ResponseEntity<?> handleResponse(ServiceResult<T> result, String jwtToken) {
        json.setHeader(HttpHeaders.AUTHORIZATION).setJwt(jwtToken).createResponse(result);
        if(result.getStatus().isError()) {
            json.setHeader(null);
            json.setJwt(null);
        }
        return ResponseEntity.status(result.getStatus()).header(HttpHeaders.AUTHORIZATION, jwtToken).body(json);
    }
}
