package dev.corusoft.slurp.common.security;

import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.infrastructure.JwtHttpConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static void secureEndpoints(HttpSecurity http) throws Exception {
        // Permitir las peticiones que indiquemos
        http.authorizeHttpRequests(requests -> requests
                // AUTH
                .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/login", "/v1/auth/login/jwt").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/v1/auth/*/password").authenticated()

                // USER
                .requestMatchers(HttpMethod.PATCH, "/v1/users/*/contact").authenticated()
                .requestMatchers(HttpMethod.GET, "/v1/users/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/v1/users/*/deactivate").authenticated()
                .requestMatchers(HttpMethod.POST, "/v1/users/*/activate").authenticated()

                // PLACES
                .requestMatchers(HttpMethod.POST, "/v1/places/findNearby").authenticated()

                // DENEGAR EL RESTO DE PETICIONES
                .anyRequest().denyAll()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtGenerator jwtGenerator) throws Exception {
        JwtHttpConfigurer jwtConfigurer = new JwtHttpConfigurer(jwtGenerator);

        http
                // Desactivar CSRF porque no usamos
                .csrf(csrf -> csrf.disable())
                // No guardar datos de la sesión del usuario
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Aplicar filtro creado para poder usar JWT
                .apply(jwtConfigurer);

        secureEndpoints(http);

        return http.build();
    }
}

