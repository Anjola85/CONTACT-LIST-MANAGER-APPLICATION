package com.example.listmanager.util.helper;

import com.example.listmanager.util.dto.ServiceResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Json Format Response Parser
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonParser<T> {

    private final String DEFAULT_HTTP_STATUS = HttpStatus.OK.value() + "";
    private final String EMPTY_STRING = "";


    @JsonProperty("header")
    private String header = null;
    @JsonProperty("jwt-token")
    private String jwt = null;
    @JsonProperty("status")
    private String status = DEFAULT_HTTP_STATUS;

    @JsonProperty("message")
    private  String message = EMPTY_STRING;

    @JsonProperty("body")
    private List<T> body = new ArrayList<>();

    public JsonParser() {

    }

    /**
     * Sets all the class variables
     * @param header
     * @param jwt
     * @param status
     * @param message
     * @param body
     */
    public JsonParser(String header, String jwt, String status, String message, List body) {
        this.header = header;
        this.jwt = jwt;
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public JsonParser(String header, String jwt, ServiceResult result) {
        this.header = header;
        this.jwt = jwt;
        this.status = result.getStatus().toString();
        this.message = result.getMessage();
        this.body = result.getData();
    }

    /**
     * sets the status, message and body
     * @param status
     * @param message
     * @param body
     */
    public JsonParser(String status, String message, List<T> body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }


    /**
     * Sets the header and jwt
     * @param header
     * @param jwt
     */
    public JsonParser(String header, String jwt) {
        this.header = header;
        this.jwt = jwt;
    }

    public String getHeader() {
        return header;
    }

    public JsonParser<T> setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getJwt() {
        return jwt;
    }

    public JsonParser<T> setJwt(String jwt) {
        this.jwt = jwt;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public JsonParser<T> setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonParser<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<T> getBody() {
        return body;
    }

    public JsonParser<T> setBody(List<T> body) {
        this.body = body;
        return this;
    }

    /**
     * Creates a custom response by mapping values
     * @param result
     * @return JsonParser object
     */
    public JsonParser<T> createResponse(ServiceResult<T> result) {

        if (result.getStatus() != null)
            this.status = result.getStatus().value() + "";

        if(result.getMessage() != null)
            this.message = result.getMessage();

        if (result.getData() != null)
            this.body = (List<T>) new ArrayList<>(result.getData());

        return this;
    }
}
