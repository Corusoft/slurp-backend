package dev.corusoft.slurp.common.config.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JacksonLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    @Serial
    private static final long serialVersionUID = 1355852411036457107L;
    private static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public JacksonLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateString = jsonParser.getValueAsString();

        return LocalDateTime.parse(dateString, formatter);
    }
}
