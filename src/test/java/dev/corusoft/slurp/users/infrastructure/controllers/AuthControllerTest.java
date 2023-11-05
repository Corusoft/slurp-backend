package dev.corusoft.slurp.users.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.common.i18n.Translator;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.UserRoles;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import dev.corusoft.slurp.utils.AuthTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static dev.corusoft.slurp.users.infrastructure.controllers.AuthApiErrorHandler.USER_ALREADY_EXISTS_KEY;
import static org.hamcrest.Matchers.*;
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
    private final Locale locale = Locale.getDefault();


    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthTestUtils authTestUtils;
    @Autowired
    private Translator translator;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CredentialRepository credentialRepo;
    @Autowired
    private ContactInfoRepository contactInfoRepo;


    /* ************************* CICLO VIDA TESTS ************************* */
    @AfterEach
    void beforeEach() {
        userRepo.deleteAll();
        credentialRepo.deleteAll();
        contactInfoRepo.deleteAll();
    }

    /* ************************* CASOS DE PRUEBA ************************* */


    @Test
    void whenRegisterUser_thenUserIsCreatedSuccessfuly() throws Exception {
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

        testResults.andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                // Valores autogenerados
                jsonPath("$.user.registeredAt").isNotEmpty(),
                jsonPath("$.user.roles").isArray(),
                jsonPath("$.user.roles", containsInAnyOrder(expectedRoles.toArray()))
        );
    }

    @Test
    void whenRegisterUserTwice_thenThrowException() throws Exception {
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
        String errorMessage = translator.generateMessage(USER_ALREADY_EXISTS_KEY, errorMessageParams, locale);

        testResults.andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())),
                jsonPath("$.timestamp", is(notNullValue())),
                jsonPath("$.message", equalTo(errorMessage))
        );
    }
}
