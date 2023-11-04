package dev.corusoft.slurp.common.config.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JacksonLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    @Serial
    private static final long serialVersionUID = 1355852411036457107L;
    private static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public JacksonLocalDateTimeSerializer() {
        this(null);
    }

    protected JacksonLocalDateTimeSerializer(Class<LocalDateTime> type) {
        super(type);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(value));
    }
}
