package dev.corusoft.slurp.places.application;

import com.google.maps.model.PlacesSearchResponse;
import dev.corusoft.slurp.TestResourcesDirectories;
import dev.corusoft.slurp.common.pagination.Block;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.places.domain.CandidateSummary;
import dev.corusoft.slurp.utils.TestResourceUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static dev.corusoft.slurp.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Log4j2
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlacesServiceTest {

    /* ************************* CONSTANTES ************************* */
    private static final String resourcesDirectory = TestResourcesDirectories.PLACES.name();
    private final String API_ENDPOINT = "/v1/places";
    private final Locale locale = LocaleContextHolder.getLocale();

    /* ************************* DEPENDENCIAS ************************* */
    @Mock
    private GoogleApiService googleApiMock;
    @Mock
    private PlacesServiceImpl placesServiceMock;

    /* ************************* CICLO VIDA TESTS ************************* */

    @BeforeEach
    public void beforeAll() {
        this.placesServiceMock = new PlacesServiceImpl(this.googleApiMock);
    }

    /* ************************* CASOS DE PRUEBA ************************* */

    @Nested
    public class FindCandidatesNearby_UseCase {

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_success.json"})
        public void when_findCandidatesNearby_thenSuccess(String expectedFile) throws Exception {
            // ** Arrange **
            PlacesCriteria searchCriteria = PlacesCriteria.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(DEFAULT_SEARCH_PERIMETER_RADIUS)
                    .build();
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);

            // ** Act **
            when(googleApiMock.findNearbyPlaces(any(PlacesCriteria.class))).thenReturn(expectedResponseFromFile);
            Block<CandidateSummary> actualResponse = placesServiceMock.findCandidatesNearby(searchCriteria);

            // ** Assert **
            assertAll(
                    // Se recibe una respuesta
                    () -> assertNotNull(actualResponse),
                    // Respuesta tiene valores
                    () -> assertEquals(20, actualResponse.getItemsCount()),
                    // Hay más valores esperados
                    () -> assertTrue(actualResponse.hasMoreItems()),
                    // Hay enlace para ver más resultados
                    () -> assertNotNull(actualResponse.getNextPageToken())
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_empty.json"})
        public void when_findCandidatesNearby_andNoMatches_thenSuccess(String expectedFile) throws Exception {
            // ** Arrange **
            PlacesCriteria searchCriteria = PlacesCriteria.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(1)
                    .build();
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);

            // ** Act **
            when(googleApiMock.findNearbyPlaces(any(PlacesCriteria.class))).thenReturn(expectedResponseFromFile);
            Block<CandidateSummary> actualResponse = placesServiceMock.findCandidatesNearby(searchCriteria);

            // ** Assert **
            assertAll(
                    // Se recibe una respuesta
                    () -> assertNotNull(actualResponse),
                    // Respuesta no tiene valores
                    () -> assertEquals(0, actualResponse.getItemsCount()),
                    // No hay más valores esperados
                    () -> assertFalse(actualResponse.hasMoreItems()),
                    // No hay enlace para ver más resultados
                    () -> assertNull(actualResponse.getNextPageToken())
            );
        }

        @Test
        public void when_findCandidatesNearby_andInvalidCriteria_thenThrowException() {
            // ** Arrange **
            PlacesCriteria searchCriteria = PlacesCriteria.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(0)
                    .build();

            // ** Act **

            // ** Assert **
            assertAll(
                    () -> assertThrows(ConstraintViolationException.class,
                            () -> placesServiceMock.findCandidatesNearby(searchCriteria)
                    )

            );
        }

    }


}
