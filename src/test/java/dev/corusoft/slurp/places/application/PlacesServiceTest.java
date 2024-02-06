package dev.corusoft.slurp.places.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.PlacesApi;
import dev.corusoft.slurp.TestResourcesDirectories;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.places.domain.SearchPerimeter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static dev.corusoft.slurp.TestConstants.DEFAULT_SEARCH_PERIMETER_RADIUS;
import static dev.corusoft.slurp.TestConstants.MADRID_KILOMETRIC_POINT_0_LONGITUDE;
import static dev.corusoft.slurp.utils.TestResourceUtils.readExpectedFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@Log4j2
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class PlacesServiceTest {
    private static final String resourcesDirectory = TestResourcesDirectories.PLACES.name();
    /* ************************* CONSTANTES ************************* */
    private final String API_ENDPOINT = "/v1/auth";
    private final Locale locale = LocaleContextHolder.getLocale();
    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;
    @Mock
    @Autowired
    private PlacesService placesServiceMock;
    @Spy
    private PlacesApi placesAPIMock;


    /* ************************* CICLO VIDA TESTS ************************* */
    @BeforeEach
    public void beforeEach() {
    }

    /* ************************* CASOS DE PRUEBA ************************* */
    private <T> T readExpectedDataFromFile(String expectedFilename, TypeReference<T> typeReference) {
        File expectedFile = readExpectedFile(resourcesDirectory, expectedFilename);

        try {
            return jsonMapper.readValue(expectedFile, typeReference);
        } catch (IOException e) {
            log.error("Error loading expected file. Path: {}", expectedFile.getPath());
            throw new RuntimeException(e);
        }
    }

    @Nested
    class FindCandidatesNearby_UseCase {

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_success.json"})
        void when_findCandidatesNearby_thenSuccess(String expectedFile) {
            SearchPerimeter perimeter = new SearchPerimeter(
                    MADRID_KILOMETRIC_POINT_0_LONGITUDE,
                    MADRID_KILOMETRIC_POINT_0_LONGITUDE,
                    DEFAULT_SEARCH_PERIMETER_RADIUS
            );

            // ** Arrange **
            Block<CandidateSummary> expectedResponse = readExpectedDataFromFile(expectedFile, new TypeReference<>() {
            });
            when(placesServiceMock.findCandidatesNearby(perimeter))
                    .thenReturn(expectedResponse);
            // TODO Mockear solo llamada a Places API
            // TODO Patrón Comando para medir cobertura de mi código y solo mockear llamada a api


            // ** Act **
            Block<CandidateSummary> actualResponse = placesServiceMock.findCandidatesNearby(perimeter);

            // ** Assert **
            assertAll(
                    // Se recibe una respuesta
                    () -> assertNotNull(actualResponse),
                    // Respuesta tiene valores
                    () -> assertTrue(actualResponse.getItemsCount() > 0),
                    // Hay más valores esperados
                    () -> assertTrue(actualResponse.hasMoreItems()),
                    // Los resultados son los esperados
                    () -> assertEquals(actualResponse.getItems(), expectedResponse.getItems())
            );
        }

    }


}
