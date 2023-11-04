package dev.corusoft.slurp.common.config.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JacksonLocalDateDeserializer extends StdDeserializer<LocalDate> {
    @Serial
    private static final long serialVersionUID = 1355852411036457107L;
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public JacksonLocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateString = jsonParser.getValueAsString();

        return LocalDate.parse(dateString, formatter);
    }
}
