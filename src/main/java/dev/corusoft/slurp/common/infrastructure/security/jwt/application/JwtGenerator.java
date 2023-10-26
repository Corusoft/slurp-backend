package dev.corusoft.slurp.common.infrastructure.security.jwt.application;

import dev.corusoft.slurp.common.infrastructure.security.jwt.domain.JwtData;

import java.io.IOException;

public interface JwtGenerator {
    String generateJWT(JwtData data) throws IOException;

    JwtData extractData(String token) throws IOException;
}
