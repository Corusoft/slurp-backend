package dev.corusoft.slurp.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Getter
@Configuration
@PropertySource("file:${user.dir}/.${spring.profiles.active}.env")
public class EnvironmentConfiguration {
    static {
        log.info("Reading environmental configuration...");
    }

    @Value("${JWT_PRIVATE_SIGN_KEY}")
    private String JWT_PRIVATE_SIGN_KEY;

    @Value("${JWT_PUBLIC_SIGN_KEY}")
    private String JWT_PUBLIC_SIGN_KEY;

    @Value("${JWT_PRIVATE_SIGN_KEY_PASSWORD}")
    private String JWT_PRIVATE_SIGN_KEY_PASSWORD;
}
