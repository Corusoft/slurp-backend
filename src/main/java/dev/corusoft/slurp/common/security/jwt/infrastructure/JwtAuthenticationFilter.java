package dev.corusoft.slurp.common.security.jwt.infrastructure;

import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.domain.JwtData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static dev.corusoft.slurp.common.security.SecurityConstants.*;

@Log4j2
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtGenerator jwtGenerator;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {
        super(authenticationManager);
        this.jwtGenerator = jwtGenerator;
    }

    /**
     * Obtiene el JWT de la cabecera HTTP AUTHORIZATION y se lo comunica a Spring para que configure el contexto del usuario
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Comprueba si se ha recibido el JWT en la cabecera de la petición
        String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaderValue == null || !authHeaderValue.startsWith(PREFIX_BEARER_TOKEN)) {
            chain.doFilter(request, response);
            log.warn("Received request without JWT in the Authorization header");
            return;
        }

        // Al obtener el JWT, extrae sus atributos y se los comunica a Spring
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);       // Obtiene los datos de acceso
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Filtra la petición según la configuración recién establecida
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // Elimina el "Bearer " de la cabecera
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authHeader.replace(PREFIX_BEARER_TOKEN, "");

        // Extraer los datos del token
        JwtData data = jwtGenerator.extractData(token);
        if (data == null) return null;

        // Agregar información del usuario al contexto solo si existen datos
        request.setAttribute(TOKEN_ATTRIBUTE_NAME, token);
        request.setAttribute(USER_ID_ATTRIBUTE_NAME, data.getUserID());

        // Asigna roles al usuario (si tiene)
        Set<GrantedAuthority> authorities = createAuthorities(data);

        return new UsernamePasswordAuthenticationToken(data, null, authorities);
    }

    private Set<GrantedAuthority> createAuthorities(JwtData token) {
        boolean hasRoles = (token.getRoles() != null) && (!token.getRoles().isEmpty());
        Set<GrantedAuthority> authorities = new HashSet<>(token.getRoles().size());

        if (!hasRoles) return authorities;

        for (String role : token.getRoles()) {
            role = role.replace("\"", "");
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ROLE_ATTRIBUTE_NAME + role);
            authorities.add(authority);
        }

        log.debug("Registering granted authorities for user {}", token.getUserID());
        return authorities;
    }
}
