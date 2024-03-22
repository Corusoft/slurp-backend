package dev.corusoft.slurp.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.common.config.JacksonConfiguration;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Log4j2
@NoArgsConstructor
public class TestResourceUtils {
    private static final String EXPECTED_FILES_ROOT_DIRECTORY = "expected";
    private static final ObjectMapper jsonMapper = JacksonConfiguration.configureObjectMapper();


    public static <T> T readDataFromFile(String directory, String filename, Class<T> clazz) {
        File expectedFile = null;
        try {
            expectedFile = openFile(directory, filename);
            JavaType type = jsonMapper.getTypeFactory().constructType(clazz);
            return jsonMapper.readValue(expectedFile, type);
        } catch (IOException e) {
            log.error("Error loading expected file.\nPath: {}\nCause: {}", expectedFile.getPath(), e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private static File openFile(String directory, String filename) throws IOException {
        String dir = (directory == null) ? "" : directory.toLowerCase();

        String resourceName = Path.of(EXPECTED_FILES_ROOT_DIRECTORY, dir, filename).toString();

        // Abrir recurso
        return openResourceAsFile(resourceName);
    }


    private static File openResourceAsFile(String resourcePath) throws IOException {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

        return resourceLoader.getResource("classpath:" + resourcePath).getFile();
    }
}
