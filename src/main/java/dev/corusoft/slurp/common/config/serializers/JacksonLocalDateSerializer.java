package dev.corusoft.slurp.common.config.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JacksonLocalDateSerializer extends StdSerializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;


    public JacksonLocalDateSerializer() {
        this(null);
    }


    protected JacksonLocalDateSerializer(Class<LocalDate> type) {
        super(type);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(value));
    }
}
