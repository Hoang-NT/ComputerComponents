package fa.training.components.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fa.training.components.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, String>, String> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<String, String> stringStringMap) {
        try {
            return objectMapper.writeValueAsString(stringStringMap);
        } catch (JsonProcessingException e) {
            throw new MyException("407", e.getMessage());
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, Map.class);
        } catch (JsonProcessingException e) {
            throw new MyException("407", e.getMessage());
        }
    }
}
