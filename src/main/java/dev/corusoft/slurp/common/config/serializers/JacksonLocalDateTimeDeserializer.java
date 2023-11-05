package dev.corusoft.slurp.common.config.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JacksonLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    private final transient DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public JacksonLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateString = jsonParser.getValueAsString();

        return LocalDateTime.parse(dateString, formatter);
    }
}
