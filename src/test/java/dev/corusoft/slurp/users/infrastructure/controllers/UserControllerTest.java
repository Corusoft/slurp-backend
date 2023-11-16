package dev.corusoft.slurp.users.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.TestUtils;
import dev.corusoft.slurp.common.i18n.Translator;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.input.UpdateContactInfoParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import dev.corusoft.slurp.utils.AuthTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static dev.corusoft.slurp.TestConstants.DEFAULT_EMAIL_DOMAIN;
import static dev.corusoft.slurp.TestConstants.DEFAULT_PHONE_NUMBER;
import static dev.corusoft.slurp.common.security.SecurityConstants.PREFIX_BEARER_TOKEN;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private TestUtils testUtils;
    @Autowired
    private AuthTestUtils authTestUtils;
    @Autowired
    private Translator translator;
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
            testUtils.assertApiResponseIsPermissionException(testResults, locale);
        }

    }


}
