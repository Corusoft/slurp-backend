package dev.corusoft.slurp.common.security;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
    /* HTTP */
    public static final String PREFIX_BEARER_TOKEN = "Bearer ";

    /* AUTHENTICATION */
    public static final String TOKEN_ATTRIBUTE_NAME = "service_token";
    public static final String USER_ID_ATTRIBUTE_NAME = "userID";
    public static final String ROLE_ATTRIBUTE_NAME = "ROLE_";
}
