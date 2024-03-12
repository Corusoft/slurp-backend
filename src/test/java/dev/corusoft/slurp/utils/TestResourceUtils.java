package dev.corusoft.slurp.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.common.config.JacksonConfiguration;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Log4j2
@NoArgsConstructor
public class TestResourceUtils {
    private static final String EXPECTED_FILES_ROOT_DIRECTORY = "expected";
    private static final ClassLoader classloaderSingleton = TestResourceUtils.class.getClassLoader();
    private static final ObjectMapper jsonMapper = JacksonConfiguration.configureObjectMapper();


    public static <T> T readDataFromFile(String directory, String filename, Class<T> clazz) {
        File expectedFile = openFile(directory, filename);

        try {
            JavaType type = jsonMapper.getTypeFactory().constructType(clazz);
            return jsonMapper.readValue(expectedFile, type);
        } catch (IOException e) {
            log.error("Error loading expected file.\nPath: {}\nCause: {}", expectedFile.getPath(), e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private static File openFile(String directory, String filename) {
        String dir = (directory == null) ? "" : directory;

        String resourceName = Path.of(EXPECTED_FILES_ROOT_DIRECTORY, dir, filename).toString();

        // Abrir recurso
        return openResourceAsFile(resourceName);
    }


    private static File openResourceAsFile(String resourcePath) {
        String filePath = classloaderSingleton.getResource(resourcePath).getFile();

        return new File(filePath);
    }
}
