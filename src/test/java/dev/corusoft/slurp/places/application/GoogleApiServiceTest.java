package dev.corusoft.slurp.places.application;

import com.google.maps.*;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import dev.corusoft.slurp.TestResourcesDirectories;
import dev.corusoft.slurp.places.application.criteria.PlacesCriteria;
import dev.corusoft.slurp.utils.TestResourceUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static dev.corusoft.slurp.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log4j2
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class GoogleApiServiceTest {

    /* ************************* CONSTANTES ************************* */
    private static final String resourcesDirectory = TestResourcesDirectories.PLACES.name();

    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private GeoApiContext geoApiContext;

    @InjectMocks
    private GoogleApiServiceImpl googleApiServiceMock;

    /* ************************* CICLO VIDA TESTS ************************* */

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);

        this.googleApiServiceMock = new GoogleApiServiceImpl(geoApiContext);
    }


    /* ************************* CASOS DE PRUEBA ************************* */

    @Nested
    public class FindNearbyPlaces_UseCase {
        // INFO: https://stackoverflow.com/a/35646116/11295728

        @ParameterizedTest
        @ValueSource(strings = {"findCandidatesNearby_success.json"})
        void when_FindNearbyPlaces_thenSuccess(String expectedFile) throws Exception {
            // ** Arrange **
            PlacesCriteria searchCriteria = PlacesCriteria.builder()
                    .latitude(MADRID_KILOMETRIC_POINT_0_LATITUDE)
                    .longitude(MADRID_KILOMETRIC_POINT_0_LONGITUDE)
                    .radius(DEFAULT_SEARCH_PERIMETER_RADIUS)
                    .build();
            LatLng position = new LatLng(MADRID_KILOMETRIC_POINT_0_LATITUDE, MADRID_KILOMETRIC_POINT_0_LONGITUDE);
            PlacesSearchResponse expectedResponseFromFile = TestResourceUtils.readDataFromFile(resourcesDirectory, expectedFile, PlacesSearchResponse.class);

            // ** Act **
            NearbySearchRequest nearbyRequestMock = mock(NearbySearchRequest.class);
            when(nearbyRequestMock.await()).thenReturn(expectedResponseFromFile);

            PlacesSearchResponse actualResponse;
            try (MockedStatic<PlacesApi> mock = mockStatic(PlacesApi.class)) {
                when(PlacesApi.nearbySearchQuery(eq(geoApiContext), eq(position)))
                        .thenReturn(nearbyRequestMock);

                actualResponse = googleApiServiceMock.findNearbyPlaces(searchCriteria);
            }

            // ** Assert **
            assertAll(
                    // Se recibe una respuesta
                    () -> assertNotNull(actualResponse),
                    // Respuesta tiene valores
                    () -> assertEquals(20, actualResponse.results.length),
                    // Resultados son los esperados (los mockeados)
                    () -> assertEquals(expectedResponseFromFile, actualResponse),
                    // Hay más valores esperados
                    () -> assertNotNull(actualResponse.nextPageToken)
            );
        }

    }
}
