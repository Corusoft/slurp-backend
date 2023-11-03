package dev.corusoft.slurp.common.security.jwt.application;

import dev.corusoft.slurp.common.security.jwt.domain.JwtData;

public interface JwtGenerator {
    String generateJWT(JwtData data);

    JwtData extractData(String token);
}
