package dev.corusoft.slurp.common.security;

import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.infrastructure.JwtHttpConfigurer;
import dev.corusoft.slurp.users.domain.UserRoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtGenerator jwtGenerator) throws Exception {
        JwtHttpConfigurer jwtConfigurer = new JwtHttpConfigurer(jwtGenerator);

        http.cors(cors -> cors.disable())
                // Desactivar CSRF porque no usamos
                .csrf(csrf -> csrf.disable())
                // No guardar datos de la sesión del usuario
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Aplicar filtro creado para poder usar JWT
                .apply(jwtConfigurer);

        // Permitir las peticiones que indiquemos
        http.authorizeHttpRequests(requests -> requests
                // ALLOWED ENDPOINTS
                // AUTH
                .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/*/password").hasAnyRole(Arrays.toString(UserRoles.values()))

                // DENEGAR EL RESTO DE PETICIONES
                .anyRequest().denyAll()
        );

        return http.build();
    }

    /**
     * Configuración de seguridad para permitir peticiones CORS.
     * También controla qué tipo de contenido aceptar en las peticiones (origen, cabeceras, método HTTP, etc)
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*");            // Permite peticiones de cualquier origen
        config.addAllowedMethod("*");            // Permite peticiones con cualquier verbo HTTP
        config.addAllowedHeader("*");            // Permite peticiones con cualquier cabecera

        // Aplica la configuración a todas las URL expuestas por el servicio
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

