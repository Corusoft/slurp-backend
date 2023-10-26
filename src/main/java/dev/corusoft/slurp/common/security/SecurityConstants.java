package dev.corusoft.slurp.common.security;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityConstants {
    /* HTTP */
    public final String PREFIX_BEARER_TOKEN = "Bearer ";

    /* AUTHENTICATION */
    public final String TOKEN_ATTRIBUTE_NAME = "service_token";
    public final String USER_ID_ATTRIBUTE_NAME = "userID";
    public final String ROLE_ATTRIBUTE_NAME = "ROLE_";
}
