package com.example.listmanager.util.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents the structure of the result object
 *
 */

@Configuration
public class ServiceResult<T> {
    @JsonProperty("status")
    private HttpStatus status = null;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("data")
    private List<T> data = null;

    public ServiceResult() {}

    public ServiceResult(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     *
     * @param status
     * @param message
     * @param data - JsonObject, contains name:value
     */
    public ServiceResult(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        // create the list for values
        this.data = new ArrayList<>();
        // adds the value to the list
        if(data != null)
            this.data.add(data);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ServiceResult<T> setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ServiceResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     *
     * @return - the list containing JsonObjects
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Accepts a data and adds it to the
     * list if elements are already contained in the list
     * @param value - value to add to list
     * @return - the result object
     */
    public ServiceResult<T> setData(T value) {
        this.data = new ArrayList<T>();
        this.data.add(value);
        return this;
    }


    /**
     * Accepts a list of data
     * @param data
     * @return
     */
    public ServiceResult<T> setData(List<T> data) {
        this.data = new ArrayList<T>();
        this.data.addAll(data);
        return this;
    }
}
