package dev.corusoft.slurp.common.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Log4j2
@Getter
@Configuration
@PropertySource("file:${user.dir}/.${spring.profiles.active}.env")
public class EnvironmentConfiguration {
    static {
        log.info("Loading environmental configuration...");
    }

    @Value("${JWT_PRIVATE_SIGN_KEY}")
    private String JWT_PRIVATE_SIGN_KEY;

    @Value("${JWT_PUBLIC_SIGN_KEY}")
    private String JWT_PUBLIC_SIGN_KEY;

    @Value("${JWT_PRIVATE_SIGN_KEY_PASSWORD}")
    private String JWT_PRIVATE_SIGN_KEY_PASSWORD;

    @Value("${GOOGLE_API_KEY}")
    private String GOOGLE_API_KEY;
}
