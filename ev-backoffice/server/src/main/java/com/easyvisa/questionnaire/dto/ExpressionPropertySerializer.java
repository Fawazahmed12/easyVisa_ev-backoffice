package com.easyvisa.questionnaire.dto;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ExpressionPropertySerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String expressionProperty, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String[] expressionProperties = expressionProperty.split(":");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(expressionProperties[0], expressionProperties[1]);
        jsonGenerator.writeEndObject();
    }
}
