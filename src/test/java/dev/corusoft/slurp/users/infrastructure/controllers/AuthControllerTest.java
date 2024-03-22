package dev.corusoft.slurp.users.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.common.Translator;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.UserRoles;
import dev.corusoft.slurp.users.infrastructure.dto.input.*;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import dev.corusoft.slurp.utils.ApiResponseUtils;
import dev.corusoft.slurp.utils.AuthTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static dev.corusoft.slurp.TestConstants.DEFAULT_NICKNAME;
import static dev.corusoft.slurp.TestConstants.DEFAULT_PASSWORD;
import static dev.corusoft.slurp.common.security.SecurityConstants.TOKEN_ATTRIBUTE_NAME;
import static dev.corusoft.slurp.common.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static dev.corusoft.slurp.users.infrastructure.controllers.UsersApiErrorHandler.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {
    /* ************************* CONSTANTES ************************* */
    private final String API_ENDPOINT = "/v1/auth";
    private final Locale locale = LocaleContextHolder.getLocale();

    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthTestUtils authTestUtils;


    /* ************************* CICLO VIDA TESTS ************************* */


    /* ************************* CASOS DE PRUEBA ************************* */
    @Nested
    class Register_UseCase {
        @Test
        void when_Register_thenSuccess() throws Exception {
            // ** Arrange **
            User validUser = authTestUtils.generateValidUser();
            RegisterUserParamsDTO paramsDTO = authTestUtils.generateRegisterParamsDtoFromUser(validUser);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/register";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            List<String> expectedRoles = List.of(UserRoles.BASIC.name());
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isCreated(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(true)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    jsonPath("$.data.serviceToken", notNullValue()),
                    // Valores autogenerados
                    jsonPath("$.data.user.registeredAt", lessThan(now)),
                    jsonPath("$.data.user.isActive", is(true)),
                    jsonPath("$.data.user.roles", containsInAnyOrder(expectedRoles.toArray()))
            );
        }

        @Test
        void when_RegisterTwice_thenThrowException() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            RegisterUserParamsDTO paramsDTO = authTestUtils.generateRegisterParamsDtoFromUser(user);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/register";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            Object[] errorMessageParams = new Object[]{paramsDTO.getNickname()};
            String errorMessage = Translator.generateMessage(USER_ALREADY_EXISTS_KEY, errorMessageParams, locale);
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(false)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    // Contenido de la respuesta de error
                    jsonPath("$.data.status", is(HttpStatus.BAD_REQUEST.name())),
                    jsonPath("$.data.statusCode", is(HttpStatus.BAD_REQUEST.value())),
                    jsonPath("$.data.message", equalTo(errorMessage)),
                    jsonPath("$.data.debugMessage", nullValue())
            );
        }

    }

    @Nested
    class Login_UseCase {
        @Test
        void when_Login_thenSuccess() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            LoginParamsDTO paramsDTO = new LoginParamsDTO(DEFAULT_NICKNAME, DEFAULT_PASSWORD);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            List<String> expectedRoles = user.getAttachedRoles().stream()
                    .map(role -> role.name())
                    .toList();

            ApiResponseUtils.assertApiResponseIsOk(testResults);
            testResults.andExpectAll(
                    jsonPath("$.data.serviceToken", notNullValue()),
                    jsonPath("$.data.user.registeredAt", lessThan(now)),
                    jsonPath("$.data.user.isActive", is(true)),
                    jsonPath("$.data.user.roles", containsInAnyOrder(expectedRoles.toArray()))
            );
        }

        @Test
        void when_Login_andUserIsDeactivated_thenSuccess() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            user.markAsUnactive();
            authTestUtils.saveOrUpdateUser(user);
            LoginParamsDTO paramsDTO = new LoginParamsDTO(DEFAULT_NICKNAME, DEFAULT_PASSWORD);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            List<String> expectedRoles = user.getAttachedRoles().stream()
                    .map(role -> role.name())
                    .toList();

            ApiResponseUtils.assertApiResponseIsOk(testResults);
            testResults.andExpectAll(
                    jsonPath("$.data.serviceToken", notNullValue()),
                    jsonPath("$.data.user.registeredAt", lessThan(now)),
                    jsonPath("$.data.user.isActive", is(true)),
                    jsonPath("$.data.user.roles", containsInAnyOrder(expectedRoles.toArray()))
            );
        }

        @Test
        void when_Login_andIncorrectPassword_thenThrowException() throws Exception {
            // ** Arrange **
            String wrongPassword = DEFAULT_PASSWORD + "XXX";
            authTestUtils.registerValidUser(DEFAULT_NICKNAME, DEFAULT_PASSWORD);
            LoginParamsDTO paramsDTO = new LoginParamsDTO(DEFAULT_NICKNAME, wrongPassword);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String errorMessage = Translator.generateMessage(INCORRECT_LOGIN_KEY, locale);
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(false)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    // Contenido de la respuesta
                    jsonPath("$.data.status", is(HttpStatus.BAD_REQUEST.name())),
                    jsonPath("$.data.statusCode", is(HttpStatus.BAD_REQUEST.value())),
                    jsonPath("$.data.message", equalTo(errorMessage)),
                    jsonPath("$.data.debugMessage", nullValue())
            );
        }

        @Test
        void when_Login_andUserDoesNotExist_thenThrowException() throws Exception {
            // ** Arrange **
            String wrongPassword = DEFAULT_PASSWORD + "XXX";
            LoginParamsDTO paramsDTO = new LoginParamsDTO(DEFAULT_NICKNAME, wrongPassword);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String errorMessage = Translator.generateMessage(INCORRECT_LOGIN_KEY, locale);
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(false)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    // Contenido de la respuesta
                    jsonPath("$.data.status", is(HttpStatus.BAD_REQUEST.name())),
                    jsonPath("$.data.statusCode", is(HttpStatus.BAD_REQUEST.value())),
                    jsonPath("$.data.message", equalTo(errorMessage)),
                    jsonPath("$.data.debugMessage", nullValue())
            );
        }

        @Test
        void when_LoginViaJWT_thenSuccess() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login/jwt";
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, user.getUserID())
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            List<String> expectedRoles = user.getAttachedRoles().stream()
                    .map(role -> role.name())
                    .toList();

            ApiResponseUtils.assertApiResponseIsOk(testResults);
            testResults.andExpectAll(
                    jsonPath("$.data.serviceToken", notNullValue()),
                    jsonPath("$.data.user.registeredAt", lessThan(now)),
                    jsonPath("$.data.user.isActive", is(true)),
                    jsonPath("$.data.user.roles", containsInAnyOrder(expectedRoles.toArray()))
            );
        }

        @Test
        void when_LoginViaJWT_andUserDoesNotExist_thenThrowException() throws Exception {
            // ** Arrange **
            UUID randomUserID = UUID.randomUUID();          // ID random para que JWT tenga campo "userID"
            User user = authTestUtils.generateValidUser();
            user.setUserID(randomUserID);
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login/jwt";
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, randomUserID)
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String errorMessage = Translator.generateMessage(USER_NOT_FOUND_KEY, locale);

            ApiResponseUtils.assertApiResponseIsNotFound(testResults, errorMessage);
        }

        @Test
        void when_LoginViaJWT_andUserIsNotCurrentUser_thenThrowException() throws Exception {
            // ** Arrange **
            UUID randomUserID = UUID.randomUUID();          // ID random para que JWT tenga campo "userID"
            User user = authTestUtils.generateValidUser();
            user.setUserID(randomUserID);
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login/jwt";
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, UUID.randomUUID())
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            ApiResponseUtils.assertApiResponseIsPermissionException(testResults, locale);
        }

        @Test
        void when_LoginViaJWT_andUserIsDeactivated_thenSuccess() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            user.markAsUnactive();
            authTestUtils.saveOrUpdateUser(user);
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/login/jwt";
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, user.getUserID())
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            ApiResponseUtils.assertApiResponseIsOk(testResults);
        }

    }

    @Nested
    class ChangePassword_UseCase {

        @Test
        void when_ChangePassword_thenSuccess() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);
            ChangePasswordParamsDTO paramsDTO = new ChangePasswordParamsDTO(DEFAULT_PASSWORD, DEFAULT_PASSWORD + "XXX");

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s/password".formatted(user.getUserID());
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = patch(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, user.getUserID())
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isNoContent(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(true)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", nullValue())
            );
        }

        @Test
        void when_ChangePassword_andPasswordDoNotMatch_thenThrowException() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);
            ChangePasswordParamsDTO paramsDTO = new ChangePasswordParamsDTO(DEFAULT_PASSWORD + "XXX", DEFAULT_PASSWORD);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s/password".formatted(user.getUserID());
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = patch(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, user.getUserID())
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String errorMessage = Translator.generateMessage(PASSWORD_DO_NOT_MATCH_KEY, locale);
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(false)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    // Contenido de la respuesta
                    jsonPath("$.data.status", is(HttpStatus.BAD_REQUEST.name())),
                    jsonPath("$.data.statusCode", is(HttpStatus.BAD_REQUEST.value())),
                    jsonPath("$.data.message", equalTo(errorMessage)),
                    jsonPath("$.data.debugMessage", nullValue())
            );
        }

        @Test
        void when_ChangePassword_andIsNotCurrentUser_thenThrowException() throws Exception {
            // ** Arrange **
            User user = authTestUtils.registerValidUser();
            AuthenticatedUserDTO authUserDto = authTestUtils.generateAuthenticatedUser(user);
            ChangePasswordParamsDTO paramsDTO = new ChangePasswordParamsDTO(DEFAULT_PASSWORD + "XXX", DEFAULT_PASSWORD);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s/password".formatted(user.getUserID());
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = patch(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, UUID.randomUUID())
                    .requestAttr(TOKEN_ATTRIBUTE_NAME, authUserDto.getServiceToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            ApiResponseUtils.assertApiResponseIsPermissionException(testResults, locale);
        }

        @Test
        void when_ChangePassword_andUserDoesNotExist_thenThrowException() throws Exception {
            // ** Arrange **
            UUID randomUserID = UUID.randomUUID();
            ChangePasswordParamsDTO paramsDTO = new ChangePasswordParamsDTO(DEFAULT_PASSWORD + "XXX", DEFAULT_PASSWORD);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s/password".formatted(randomUserID);
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = patch(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, randomUserID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String errorMessage = Translator.generateMessage(USER_NOT_FOUND_KEY, locale);

            ApiResponseUtils.assertApiResponseIsNotFound(testResults, errorMessage);
        }
    }
}
