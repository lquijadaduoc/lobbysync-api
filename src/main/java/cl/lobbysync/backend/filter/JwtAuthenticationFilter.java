package cl.lobbysync.backend.filter;

import cl.lobbysync.backend.service.JwtKeyService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtKeyService jwtKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no hay header Authorization, continuar sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            // Intentar parsear como JWT del backend  
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) jwtKeyService.getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);
            String firebaseUid = claims.get("firebaseUid", String.class);

            if (email != null) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }

                // Crear el objeto Authentication con el email como principal
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Agregar detalles adicionales
                authentication.setDetails(claims);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT token validated for user: {} (userId: {}, role: {})", email, userId, role);
            }

        } catch (Exception e) {
            // Si el JWT no es válido, no hacer nada (puede ser un token de Firebase)
            log.debug("JWT validation failed (may be Firebase token): {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
