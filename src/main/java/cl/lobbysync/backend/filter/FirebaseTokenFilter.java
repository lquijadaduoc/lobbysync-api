package cl.lobbysync.backend.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (firebaseAuth != null && authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
                
                String uid = decodedToken.getUid();
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        uid, 
                        null, 
                        new ArrayList<>()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (FirebaseAuthException e) {
                logger.error("Firebase token verification failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
