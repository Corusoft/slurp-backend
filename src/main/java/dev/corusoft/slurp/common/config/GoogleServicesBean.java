package dev.corusoft.slurp.common.config;

import com.google.maps.GeoApiContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;

@Slf4j
@Getter
@Configuration
public class GoogleServicesBean {
    private final EnvironmentConfiguration envConfig;
    public GeoApiContext geoApiContext;

    public GoogleServicesBean(EnvironmentConfiguration envConfig) {
        this.envConfig = envConfig;
    }

    static {
        log.info("Loading Google services configuration...");
    }


    @Bean
    @Primary
    public GeoApiContext initializeGeoApiContext() {
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(envConfig.getGOOGLE_API_KEY())
                .build();
        log.info("Configured GeoApiContext");

        return geoApiContext;
    }

    @PreDestroy
    public void freeGeoApiContextResources() {
        log.info("Shut down GeoApiContext");
        geoApiContext.shutdown();
    }
}
