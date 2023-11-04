package dev.corusoft.slurp.users.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.UserRoles;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.utils.AuthTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {
    /* ************************* CONSTANTES ************************* */
    private final String API_ENDPOINT = "/v1/auth";
    private final Locale lcl = Locale.getDefault();


    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthTestUtils authTestUtils;


    /* ************************* CICLO VIDA TESTS ************************* */


    /* ************************* CASOS DE PRUEBA ************************* */


    @Test
    void whenRegisterNewUser_thenUserIsCreatedSuccessfuly() throws Exception {
        // ** Arrange **
        User validUser = authTestUtils.generateValidUser();
        RegisterUserParamsDTO paramsDTO = authTestUtils.generateRegisterParamsDtoFromUser(validUser);

        // ** Act **
        String endpointAddress = API_ENDPOINT + "/register";
        String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
        RequestBuilder requestBuilder = post(endpointAddress)
                .contentType(MediaType.APPLICATION_JSON)
                .content(encodedRequestBody)
                .locale(lcl);
        ResultActions actionsToTest = mockMvc.perform(requestBuilder);
        List<String> expectedRoles = List.of(UserRoles.BASIC.name());

        // ** Assert **
        actionsToTest.andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                // Valores autogenerados
                jsonPath("$.user.registeredAt").isNotEmpty(),
                jsonPath("$.user.roles").isArray(),
                jsonPath("$.user.roles", containsInAnyOrder(expectedRoles.toArray()))
        );
    }
}
