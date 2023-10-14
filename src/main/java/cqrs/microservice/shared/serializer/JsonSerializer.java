package cqrs.microservice.shared.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqrs.microservice.order.exceptions.DeserializeFromJsonBytesException;
import cqrs.microservice.order.exceptions.SerializeToJsonBytesException;

import java.io.IOException;

public final class JsonSerializer {
    private final ObjectMapper objectMapper;

    public JsonSerializer(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public byte[] serializeToJsonBytes(final Object object){
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e){
            throw new SerializeToJsonBytesException(e.getMessage(), e);
        }
    }

    public <T> T deserializeFromJsonBytes(final byte[] jsonBytes, final Class<T> valueType){
        try {
            return objectMapper.readValue(jsonBytes, valueType);
        } catch (IOException e){
            throw new DeserializeFromJsonBytesException(e.getMessage(), e);
        }
    }
}
