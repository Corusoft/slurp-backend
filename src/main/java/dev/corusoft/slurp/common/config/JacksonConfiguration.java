package dev.corusoft.slurp.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.corusoft.slurp.common.config.serializers.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class JacksonConfiguration {
    @NotNull
    public static JavaTimeModule configureTimeModules() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        javaTimeModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        return javaTimeModule;
    }

    @Bean
    @Primary
    public static ObjectMapper configureObjectMapper() {
        JavaTimeModule javaTimeModule = configureTimeModules();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

}
