package de.jverhoelen.cryptoalerts.datautils.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class MapConverter implements AttributeConverter<Map<String, Double>, String> {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(Map<String, Double> stringDoubleMap) {
        try {
            return objectMapper.writeValueAsString(stringDoubleMap);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Override
    public Map<String, Double> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, new TypeReference<Map<String, Double>>() {
            });
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
