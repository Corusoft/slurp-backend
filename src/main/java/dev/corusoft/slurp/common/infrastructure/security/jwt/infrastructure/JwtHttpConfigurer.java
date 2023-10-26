package dev.corusoft.slurp.common.infrastructure.security.jwt.infrastructure;

import dev.corusoft.slurp.common.infrastructure.security.jwt.application.JwtGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtHttpConfigurer extends AbstractHttpConfigurer<JwtHttpConfigurer, HttpSecurity> {
    private JwtGenerator jwtGenerator;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        final AuthenticationManager authManager = http.getSharedObject(AuthenticationManager.class);
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(authManager, jwtGenerator);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
