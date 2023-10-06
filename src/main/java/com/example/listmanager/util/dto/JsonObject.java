package com.example.listmanager.util.dto;

import com.example.listmanager.util.helper.DynamicJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

public class JsonObject<T> {
    private String name;
    private List<T> values = new ArrayList<>();

    public JsonObject() {}

    /**
     * Response object containing one value
     * @param name
     * @param value
     */
    public JsonObject(String name, T value) {
        this.name = name;
        values = new ArrayList<T>();
        values.add(value);
    }

    /**
     * Response object containing multiple values
     * @param name
     * @param values
     */
    public JsonObject(String name, List<T> values) {
        this.name = name;
        values = new ArrayList<T>();
        values.addAll(values);
    }

    @JsonSerialize(using = DynamicJsonSerializer.class)
    public Object getValue() {
        return values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds value to the array
     * @param value
     */
    public void setValue(T value) {
        if(values.size() == 0) {
            values = new ArrayList<T>();
        }
        values.add(value);
    }
}
