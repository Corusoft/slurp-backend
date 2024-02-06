package dev.corusoft.slurp.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Component
public class TestResourceUtils {
    private static final String EXPECTED_FILES_ROOT_DIRECTORY = "expected";
    private static ClassLoader classloaderSingleton;


    static {
        if (classloaderSingleton == null) {
            classloaderSingleton = TestResourceUtils.class.getClassLoader();
        }
    }


    public TestResourceUtils() {
    }

    private static File readExpectedFile(String filename) {
        return readExpectedFile(null, filename);
    }

    public static File readExpectedFile(String directory, String filename) {
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
