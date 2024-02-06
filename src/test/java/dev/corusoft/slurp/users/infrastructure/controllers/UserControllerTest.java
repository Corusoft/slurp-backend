package dev.corusoft.slurp.users.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.common.i18n.Translator;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.input.UpdateContactInfoParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.UserDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRepository;
import dev.corusoft.slurp.utils.ApiResponseUtils;
import dev.corusoft.slurp.utils.AuthTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import static dev.corusoft.slurp.TestConstants.DEFAULT_EMAIL_DOMAIN;
import static dev.corusoft.slurp.TestConstants.DEFAULT_PHONE_NUMBER;
import static dev.corusoft.slurp.common.security.SecurityConstants.PREFIX_BEARER_TOKEN;
import static dev.corusoft.slurp.users.infrastructure.controllers.UsersApiErrorHandler.USER_IS_DEACTIVATED_KEY;
import static dev.corusoft.slurp.users.infrastructure.controllers.UsersApiErrorHandler.USER_NOT_FOUND_KEY;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = true)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerTest {
    /* ************************* CONSTANTES ************************* */
    private final String API_ENDPOINT = "/v1/users";
    private final Locale locale = Locale.getDefault();


    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApiResponseUtils apiResponseUtils;
    @Autowired
    private AuthTestUtils authTestUtils;
    @Autowired
    private Translator translator;
    @Autowired
    private UserRepository userRepo;

    /* ************************* CASOS DE PRUEBA ************************* */
    private User registeredUser;
    private AuthenticatedUserDTO registeredAuthUserDTO = null;
    private String bearerToken = null;
    // Datos de un segundo usuario
    private User otherRegisteredUser;
    private AuthenticatedUserDTO otherRegisteredAuthUserDTO = null;
    private String otherBearerToken = null;

    /* ************************* CICLO VIDA TESTS ************************* */
    @BeforeEach
    void beforeEach() {
        this.registeredUser = authTestUtils.registerValidUser("USER", "PASSWORD");
        if (this.registeredUser != null) {
            this.registeredAuthUserDTO = authTestUtils.generateAuthenticatedUser(registeredUser);
            this.bearerToken = PREFIX_BEARER_TOKEN + this.registeredAuthUserDTO.getServiceToken();
        }

        // Datos de un segundo usuario
        this.otherRegisteredUser = authTestUtils.registerValidUser("OTHER", "OTHER");
        if (this.otherRegisteredUser != null) {
            this.otherRegisteredAuthUserDTO = authTestUtils.generateAuthenticatedUser(otherRegisteredUser);
            this.otherBearerToken = PREFIX_BEARER_TOKEN + this.otherRegisteredAuthUserDTO.getServiceToken();
        }
    }

    @AfterEach
    void afterEach() {
        authTestUtils.removeRegisteredUser(this.registeredUser);
        this.registeredAuthUserDTO = null;
        this.bearerToken = null;
    }

    @Nested
    class UpdateContactInfo_UseCase {
        @Test
        void when_UpdateContactInfo_thenSuccess() throws Exception {
            // ** Arrange **
            String newEmail = "XXX@" + DEFAULT_EMAIL_DOMAIN;
            String newPhoneNumber = new StringBuilder(DEFAULT_PHONE_NUMBER).reverse().toString();
            UpdateContactInfoParamsDTO paramsDTO = UpdateContactInfoParamsDTO.builder()
                    .newEmail(newEmail)
                    .newMobilePhone(newPhoneNumber)
                    .build();

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s/contact".formatted(registeredUser.getUserID());
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = patch(endpointAddress)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            testResults.andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(true)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    // Datos de la respuesta
                    jsonPath("$.data.contactInfo", notNullValue()),
                    jsonPath("$.data.contactInfo.email", is(newEmail)),
                    jsonPath("$.data.contactInfo.isEmailVerified", notNullValue()),
                    jsonPath("$.data.contactInfo.phoneNumber", is(newPhoneNumber)),
                    jsonPath("$.data.contactInfo.phoneNumber", notNullValue())
            );
        }

        @Test
        void when_UpdateContactInfo_andIsNotCurrentUser_thenThrowException() throws Exception {
            // ** Arrange **
            String newEmail = "XXX@" + DEFAULT_EMAIL_DOMAIN;
            String newPhoneNumber = new StringBuilder(DEFAULT_PHONE_NUMBER).reverse().toString();
            UpdateContactInfoParamsDTO paramsDTO = UpdateContactInfoParamsDTO.builder()
                    .newEmail(newEmail)
                    .newMobilePhone(newPhoneNumber)
                    .build();

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s/contact".formatted(registeredUser.getUserID());
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = patch(endpointAddress)
                    .header(HttpHeaders.AUTHORIZATION, otherBearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedRequestBody)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            apiResponseUtils.assertApiResponseIsPermissionException(testResults, locale);
        }
    }

    @Nested
    class FindUser_UseCase {
        @Test
        void when_FindUserById_thenSuccess() throws Exception {
            // ** Arrange **

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s".formatted(registeredUser.getUserID());
            RequestBuilder requestBuilder = get(endpointAddress)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            UserDTO userDTO = registeredAuthUserDTO.getUserDTO();
            String userBirthDateString = DateTimeFormatter.ISO_DATE.format(userDTO.getBirthDate());

            testResults.andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(true)),
                    jsonPath("$.timestamp", lessThan(now)),
                    // Datos de la respuesta
                    jsonPath("$.data.userID", is(userDTO.getUserID().toString())),
                    jsonPath("$.data.name", is(userDTO.getName())),
                    jsonPath("$.data.surname", is(userDTO.getSurname())),
                    jsonPath("$.data.gender", is(userDTO.getGender().name())),
                    jsonPath("$.data.nickname", is(userDTO.getNickname())),
                    jsonPath("$.data.birthDate", is(userBirthDateString)),
                    jsonPath("$.data.isActive", is(true)),
                    jsonPath("$.data.registeredAt", lessThan(now)),
                    jsonPath("$.data.roles", hasSize(userDTO.getRoles().size())),
                    jsonPath("$.data.contactInfo", notNullValue())
            );
        }

        @Test
        void when_FindUserById_andIsAnotherUser_thenSuccess() throws Exception {
            // ** Arrange **

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s".formatted(otherRegisteredUser.getUserID());
            RequestBuilder requestBuilder = get(endpointAddress)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            UserDTO userDTO = otherRegisteredAuthUserDTO.getUserDTO();
            String userBirthDateString = DateTimeFormatter.ISO_DATE.format(userDTO.getBirthDate());

            testResults.andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(true)),
                    jsonPath("$.timestamp", lessThan(now)),
                    // Datos de la respuesta
                    jsonPath("$.data.userID", is(userDTO.getUserID().toString())),
                    jsonPath("$.data.name", is(userDTO.getName())),
                    jsonPath("$.data.surname", is(userDTO.getSurname())),
                    jsonPath("$.data.gender", is(userDTO.getGender().name())),
                    jsonPath("$.data.nickname", is(userDTO.getNickname())),
                    jsonPath("$.data.birthDate", is(userBirthDateString)),
                    jsonPath("$.data.registeredAt", lessThan(now)),
                    jsonPath("$.data.isActive", is(true)),
                    jsonPath("$.data.roles", hasSize(userDTO.getRoles().size())),
                    jsonPath("$.data.contactInfo", notNullValue())
            );
        }

        @Test
        void when_FindUserById_andIsDeactivated_thenThrowException() throws Exception {
            // ** Arrange **
            registeredUser.markAsUnactive();
            userRepo.save(registeredUser);

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s".formatted(registeredUser.getUserID());
            RequestBuilder requestBuilder = get(endpointAddress)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            String errorMessage = translator.generateMessage(USER_IS_DEACTIVATED_KEY, locale);

            testResults.andExpectAll(
                    status().isUnauthorized(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(false)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    jsonPath("$.data.status", is(HttpStatus.UNAUTHORIZED.name())),
                    jsonPath("$.data.statusCode", is(HttpStatus.UNAUTHORIZED.value())),
                    jsonPath("$.data.message", equalTo(errorMessage)),
                    jsonPath("$.data.debugMessage", nullValue())
            );
        }

        @Test
        void when_FindUserById_andUserDoesNotExist_thenThrowException() throws Exception {
            // ** Arrange **

            // ** Act **
            String endpointAddress = API_ENDPOINT + "/%s".formatted(UUID.randomUUID());
            RequestBuilder requestBuilder = get(endpointAddress)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            String errorMessage = translator.generateMessage(USER_NOT_FOUND_KEY, locale);

            testResults.andExpectAll(
                    status().isNotFound(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.success", is(false)),
                    jsonPath("$.timestamp", lessThan(now)),
                    jsonPath("$.data", notNullValue()),
                    jsonPath("$.data.status", is(HttpStatus.NOT_FOUND.name())),
                    jsonPath("$.data.statusCode", is(HttpStatus.NOT_FOUND.value())),
                    jsonPath("$.data.message", equalTo(errorMessage)),
                    jsonPath("$.data.debugMessage", nullValue())
            );
        }

    }

    @Nested
    class UserActivation_UseCase {
        @Nested
        class ActivateUser {
            @Test
            void when_ActivateUser_thenSuccess() throws Exception {
                // ** Arrange **

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/activate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = post(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

                testResults.andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success", is(true)),
                        jsonPath("$.timestamp", lessThan(now)),
                        // Datos de la respuesta
                        jsonPath("$.data.isActive", is(true))
                );

            }

            @Test
            void when_ActivateUser_andIsNotCurrentUser_thenThrowException() throws Exception {
                // ** Arrange **

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/activate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = post(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, otherBearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                apiResponseUtils.assertApiResponseIsPermissionException(testResults, locale);
            }

            @Test
            void when_ActivateUser_andUserDoesNotExist_thenThrowException() throws Exception {
                // ** Arrange **
                authTestUtils.removeRegisteredUser(registeredUser);

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/activate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = post(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
                String errorMessage = translator.generateMessage(USER_NOT_FOUND_KEY, locale);

                testResults.andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success", is(false)),
                        jsonPath("$.timestamp", lessThan(now)),
                        jsonPath("$.data", notNullValue()),
                        jsonPath("$.data.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.data.statusCode", is(HttpStatus.NOT_FOUND.value())),
                        jsonPath("$.data.message", equalTo(errorMessage)),
                        jsonPath("$.data.debugMessage", nullValue())
                );
            }

            @Test
            void when_ActivateUser_andUserWasDesactivated_thenSuccess() throws Exception {
                // ** Arrange **
                registeredUser.markAsUnactive();
                userRepo.save(registeredUser);

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/activate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = post(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

                testResults.andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success", is(true)),
                        jsonPath("$.timestamp", lessThan(now)),
                        // Datos de la respuesta
                        jsonPath("$.data.isActive", is(true))
                );
            }
        }

        @Nested
        class DeactivateUser {

            @Test
            void when_DeactivateUser_thenSuccess() throws Exception {
                // ** Arrange **

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/deactivate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = delete(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                apiResponseUtils.assertApiResponseIsSuccessWithEmptyData(testResults, locale);
            }

            @Test
            void when_DeactivateUser_andIsNotCurrentUser_thenThrowException() throws Exception {
                // ** Arrange **

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/deactivate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = delete(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, otherBearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                apiResponseUtils.assertApiResponseIsPermissionException(testResults, locale);
            }

            @Test
            void when_DeactivateUser_andUserDoesNotExist_thenThrowException() throws Exception {
                // ** Arrange **
                authTestUtils.removeRegisteredUser(registeredUser);

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/deactivate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = delete(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
                String errorMessage = translator.generateMessage(USER_NOT_FOUND_KEY, locale);

                testResults.andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success", is(false)),
                        jsonPath("$.timestamp", lessThan(now)),
                        jsonPath("$.data", notNullValue()),
                        jsonPath("$.data.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.data.statusCode", is(HttpStatus.NOT_FOUND.value())),
                        jsonPath("$.data.message", equalTo(errorMessage)),
                        jsonPath("$.data.debugMessage", nullValue())
                );
            }

            @Test
            void when_DeactivateUser_andUserIsAlreadyDeactivated_thenSuccess() throws Exception {
                // ** Arrange **

                // ** Act **
                String endpointAddress = API_ENDPOINT + "/%s/deactivate".formatted(registeredUser.getUserID());
                RequestBuilder requestBuilder = delete(endpointAddress)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .locale(locale);
                ResultActions testResults = mockMvc.perform(requestBuilder);

                // ** Assert **
                apiResponseUtils.assertApiResponseIsSuccessWithEmptyData(testResults, locale);
            }
        }

    }
}
