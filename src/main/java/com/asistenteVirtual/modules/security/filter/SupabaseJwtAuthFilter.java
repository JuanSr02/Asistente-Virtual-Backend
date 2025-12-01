package com.asistenteVirtual.modules.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupabaseJwtAuthFilter extends OncePerRequestFilter {

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            // Nota: En producción real, considera cachear el 'Algorithm' si es posible, aunque aquí es ligero.
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            String userId = jwt.getSubject();
            // Validar que el claim exista para evitar NullPointerException
            String role = jwt.getClaim("rol_usuario").asString();
            if (role == null) role = "USER"; // Fallback seguro

            var authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                    { "message": "Token inválido o expirado", "status": 401 }
                    """);
        }
    }

    @Override
    protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/favicon.ico");
    }
}