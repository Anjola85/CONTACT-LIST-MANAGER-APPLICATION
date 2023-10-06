package com.example.listmanager.util.helper;

import com.example.listmanager.util.dto.JsonObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DynamicJsonSerializer extends JsonSerializer<JsonObject> {

    /**
     *
     * @param value
     * @param gen
     * @param serializers
     * @throws IOException
     */
    @Override
    public void serialize(JsonObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField(value.getName(), value.getValue());
        gen.writeEndObject();
    }
}

