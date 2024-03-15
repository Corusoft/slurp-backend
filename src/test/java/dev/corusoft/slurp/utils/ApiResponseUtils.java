package dev.corusoft.slurp.utils;

import dev.corusoft.slurp.common.Translator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static dev.corusoft.slurp.users.infrastructure.controllers.UsersApiErrorHandler.PERMISSION_KEY;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiResponseUtils {

    public static void assertApiResponseIsPermissionException(ResultActions testResults, Locale locale) throws Exception {
        String errorMessage = Translator.generateMessage(PERMISSION_KEY, locale);
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        testResults.andExpectAll(
                status().isForbidden(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.success", is(false)),
                jsonPath("$.timestamp", lessThan(now)),
                jsonPath("$.data", notNullValue()),
                // Contenido de la respuesta
                jsonPath("$.data.status", is(HttpStatus.FORBIDDEN.name())),
                jsonPath("$.data.statusCode", is(HttpStatus.FORBIDDEN.value())),
                jsonPath("$.data.message", equalTo(errorMessage)),
                jsonPath("$.data.debugMessage", nullValue())
        );
    }

    public static void assertApiResponseIsSuccessWithEmptyData(ResultActions testResults) throws Exception {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        testResults.andExpectAll(
                status().isNoContent(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.success", is(true)),
                jsonPath("$.timestamp", lessThan(now)),
                jsonPath("$.data", nullValue())
        );
    }

    public static void assertApiResponseIsOk(ResultActions testResults) throws Exception {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        testResults.andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.success", is(true)),
                jsonPath("$.timestamp", lessThan(now)),
                jsonPath("$.data", notNullValue())
        );
    }


}
