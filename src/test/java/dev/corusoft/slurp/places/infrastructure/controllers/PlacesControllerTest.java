package dev.corusoft.slurp.places.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import dev.corusoft.slurp.TestResourcesDirectories;
import dev.corusoft.slurp.common.Translator;
import dev.corusoft.slurp.common.api.error.ServiceException;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.common.pagination.BlockDTO;
import dev.corusoft.slurp.places.application.PlacesService;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.infrastructure.dto.CandidateSummaryDTO;
import dev.corusoft.slurp.places.infrastructure.dto.PlacesCriteriaDTO;
import dev.corusoft.slurp.places.infrastructure.dto.conversors.CandidateConversor;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import dev.corusoft.slurp.utils.*;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static dev.corusoft.slurp.TestConstants.*;
import static dev.corusoft.slurp.common.CommonControllerAdvice.INVALID_ARGUMENT_KEY;
import static dev.corusoft.slurp.common.CommonControllerAdvice.MISSING_MANDATORY_VALUE_KEY;
import static dev.corusoft.slurp.common.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ComponentScan(basePackages = {"dev.corusoft.slurp.common.config"})
@Log4j2
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class PlacesControllerTest {

    /* ************************* CONSTANTES ************************* */
    private static final String resourcesDirectory = TestResourcesDirectories.PLACES.name();
    private final String API_ENDPOINT = "/v1/places";
    private final Locale locale = LocaleContextHolder.getLocale();


    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthTestUtils authTestUtils;
    @SpyBean
    private PlacesService placesServiceMock;

    /* ************************* CICLO VIDA TESTS ************************* */
    private User currentUser;
    private AuthenticatedUserDTO currentUserAuth;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);

        currentUser = authTestUtils.registerValidUser();
        currentUserAuth = authTestUtils.generateAuthenticatedUser(currentUser);
    }

    @AfterEach
    void afterEach() {
        authTestUtils.removeAllRegisteredUsers();
    }

    /* ************************* CASOS DE PRUEBA ************************* */

    @Nested
    class findCandidatesNearby_UseCase {
        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_success.json"})
        void when_findCandidatesNearby_thenSuccess(String expectedFile) throws Exception {
            // ** Arrange **
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesCriteriaDTO paramsDTO = PlacesCriteriaDTO.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(DEFAULT_SEARCH_PERIMETER_RADIUS)
                    .build();

            // ** Act **
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);
            Block<CandidateSummary> mockedResponse = PlacesTestUtils.createBlockOfCandidateSummary(expectedResponseFromFile, position);
            doReturn(mockedResponse).when(placesServiceMock).findCandidatesNearby(any(PlacesCriteria.class));

            String endpointAddress = API_ENDPOINT + "/findNearby";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, currentUserAuth.getUserDTO().getUserID())
                    .content(encodedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            List<CandidateSummaryDTO> candidateSummaryDTOS = CandidateConversor.toCandidateSummaryDTOList(mockedResponse.getItems());
            BlockDTO<CandidateSummaryDTO> expectedResponse = new BlockDTO<>(candidateSummaryDTOS, mockedResponse.getNextPageToken());

            ApiResponseUtils.assertApiResponseIsOk(testResults);
            testResults.andExpectAll(
                    jsonPath("$.data.hasMoreItems", is(true)),
                    jsonPath("$.data.itemsCount", is(expectedResponse.getItemsCount())),
                    jsonPath("$.data.nextPageToken", is(expectedResponse.getNextPageToken()))
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_success.json"})
        void when_findCandidatesNearby_andUsingAllCriterias_thenSuccess(String expectedFile) throws Exception {
            // ** Arrange **
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesCriteriaDTO paramsDTO = PlacesCriteriaDTO.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(DEFAULT_SEARCH_PERIMETER_RADIUS)
                    .isOpenNow(true)
                    .keywords("keywords")
                    .minPriceLevel("FREE")
                    .maxPriceLevel("VERY_EXPENSIVE")
                    .placeType("BAR")
                    .build();

            // ** Act **
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);
            Block<CandidateSummary> mockedResponse = PlacesTestUtils.createBlockOfCandidateSummary(expectedResponseFromFile, position);
            doReturn(mockedResponse).when(placesServiceMock).findCandidatesNearby(any(PlacesCriteria.class));

            String endpointAddress = API_ENDPOINT + "/findNearby";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, currentUserAuth.getUserDTO().getUserID())
                    .content(encodedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            List<CandidateSummaryDTO> candidateSummaryDTOS = CandidateConversor.toCandidateSummaryDTOList(mockedResponse.getItems());
            BlockDTO<CandidateSummaryDTO> expectedResponse = new BlockDTO<>(candidateSummaryDTOS, mockedResponse.getNextPageToken());

            ApiResponseUtils.assertApiResponseIsOk(testResults);
            testResults.andExpectAll(
                    jsonPath("$.data.hasMoreItems", is(true)),
                    jsonPath("$.data.itemsCount", is(expectedResponse.getItemsCount())),
                    jsonPath("$.data.nextPageToken", is(expectedResponse.getNextPageToken()))
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_empty.json"})
        void when_findCandidatesNearby_andNoCandidatesFound_thenSuccess(String expectedFile) throws Exception {
            // ** Arrange **
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesCriteriaDTO paramsDTO = PlacesCriteriaDTO.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(DEFAULT_SEARCH_PERIMETER_RADIUS)
                    .build();

            // ** Act **
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);
            Block<CandidateSummary> mockedResponse = PlacesTestUtils.createBlockOfCandidateSummary(expectedResponseFromFile, position);
            doReturn(mockedResponse).when(placesServiceMock).findCandidatesNearby(any(PlacesCriteria.class));

            String endpointAddress = API_ENDPOINT + "/findNearby";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, currentUserAuth.getUserDTO().getUserID())
                    .content(encodedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            ApiResponseUtils.assertApiResponseIsOk(testResults);
            testResults.andExpectAll(
                    jsonPath("$.data.hasMoreItems", is(false)),
                    jsonPath("$.data.itemsCount", is(0)),
                    jsonPath("$.data.items", emptyIterable()),
                    jsonPath("$.data.nextPageToken", nullValue())
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_empty.json"})
        void when_findCandidatesNearby_andInvalidCriteria_thenException(@Valid String expectedFile) throws Exception {
            // ** Arrange **
            Integer INVALID_RADIUS = -1;
            String placesCriteriaClassname = PlacesCriteria.class.getSimpleName();
            String INVALID_FIELDNAME = "radius";
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesCriteriaDTO paramsDTO = PlacesCriteriaDTO.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(INVALID_RADIUS)
                    .build();

            // ** Act **
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);
            Block<CandidateSummary> mockedResponse = PlacesTestUtils.createBlockOfCandidateSummary(expectedResponseFromFile, position);

            String endpointAddress = API_ENDPOINT + "/findNearby";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, currentUserAuth.getUserDTO().getUserID())
                    .content(encodedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            Object[] errorMessageParams = new Object[]{INVALID_RADIUS, placesCriteriaClassname, INVALID_FIELDNAME};
            String errorMessage = Translator.generateMessage(INVALID_ARGUMENT_KEY, errorMessageParams, locale);

            ApiResponseUtils.assertApiResponseIsBadRequest(testResults, errorMessage);
        }

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_empty.json"})
        void when_findCandidatesNearby_andMissingCriteria_thenException(@Valid String expectedFile) throws Exception {
            // ** Arrange **
            String MISSING_FIELDNAME = "latitude";
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesCriteriaDTO paramsDTO = PlacesCriteriaDTO.builder().build();

            // ** Act **
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);
            Block<CandidateSummary> mockedResponse = PlacesTestUtils.createBlockOfCandidateSummary(expectedResponseFromFile, position);

            String endpointAddress = API_ENDPOINT + "/findNearby";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, currentUserAuth.getUserDTO().getUserID())
                    .content(encodedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            Object[] errorMessageParams = new Object[]{MISSING_FIELDNAME};
            String errorMessage = Translator.generateMessage(MISSING_MANDATORY_VALUE_KEY, errorMessageParams, locale);
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            ApiResponseUtils.assertApiResponseIsBadRequest(testResults, errorMessage);
        }

        @Test
        void when_findCandidatesNearby_andInternalError_thenException() throws Exception {
            // ** Arrange **
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesCriteriaDTO paramsDTO = PlacesCriteriaDTO.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(DEFAULT_SEARCH_PERIMETER_RADIUS)
                    .build();

            // ** Act **
            doThrow(ServiceException.class).when(placesServiceMock).findCandidatesNearby(any(PlacesCriteria.class));

            String endpointAddress = API_ENDPOINT + "/findNearby";
            String encodedRequestBody = jsonMapper.writeValueAsString(paramsDTO);
            RequestBuilder requestBuilder = post(endpointAddress)
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, currentUserAuth.getUserDTO().getUserID())
                    .content(encodedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .locale(locale);
            ResultActions testResults = mockMvc.perform(requestBuilder);

            // ** Assert **
            ApiResponseUtils.assertApiResponseIsServiceException(testResults, locale);
        }

    }
}
