package dev.corusoft.slurp.common.infrastructure.security.jwt.application;


import dev.corusoft.slurp.common.infrastructure.security.crypto.RSAKeyManager;
import dev.corusoft.slurp.common.infrastructure.security.jwt.domain.JwtData;
import dev.corusoft.slurp.common.infrastructure.security.jwt.domain.JwtDataVisitor;
import io.jsonwebtoken.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Component
@Primary
public class JwtGeneratorImpl implements JwtGenerator {
    @Value("${project.security.jwt.expirationInMilliseconds}")
    private long expirationInMilliseconds;
    @Value("${spring.application.name}")
    private String applicationName;


    public static Map<String, Object> toClaims(JwtData data) {
        return JwtDataVisitor.toMap(data);
    }

    @Override
    public String generateJWT(JwtData data) {
        Date currentDate = new Date();
        Date tokenExpirationDate = new Date(System.currentTimeMillis() + expirationInMilliseconds);
        PrivateKey privateKey = RSAKeyManager.loadKeyPair().getPrivate();

        Map<String, Object> customClaims = toClaims(data);

        return Jwts.builder()
                //.signWith(publicKey)
                .signWith(privateKey)
                .issuer(applicationName)
                .subject(applicationName)
                .expiration(tokenExpirationDate)
                .issuedAt(currentDate)
                .notBefore(currentDate)
                .header()
                .empty()
                .add("typ", "JWT")
                .and()
                .claim("claims", customClaims)
                .compact();
    }

    @Override
    public JwtData extractData(String token) {
        PublicKey publicKey = RSAKeyManager.loadKeyPair().getPublic();

        JwtParser jwtParser = Jwts.parser()
                .requireIssuer(applicationName)
                .requireSubject(applicationName)
                .verifyWith(publicKey)
                .build();

        Claims claims = jwtParser
                .parseSignedClaims(token)
                .getPayload();

        return JwtDataVisitor.fromMap(claims);
    }
}
