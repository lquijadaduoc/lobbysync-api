package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.GoogleUserInfo;
import cl.lobbysync.backend.exception.UnauthorizedException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class GoogleAuthService {

    @Value("${google.oauth.client-id:}")
    private String clientId;

    private GoogleIdTokenVerifier verifier;

    /**
     * Valida un Google ID Token y extrae la información del usuario
     */
    public GoogleUserInfo verifyGoogleToken(String idTokenString) {
        try {
            // Inicializar verifier si no existe
            if (verifier == null) {
                if (clientId == null || clientId.isEmpty()) {
                    throw new UnauthorizedException("Google OAuth no está configurado. Configura 'google.oauth.client-id' en application.properties");
                }
                
                verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(clientId))
                        .build();
            }

            // Verificar el token
            GoogleIdToken idToken = verifier.verify(idTokenString);
            
            if (idToken == null) {
                throw new UnauthorizedException("Token de Google inválido o expirado");
            }

            // Extraer información del payload
            GoogleIdToken.Payload payload = idToken.getPayload();
            
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String googleId = payload.getSubject();
            String fullName = (String) payload.get("name");
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            log.info("Google token verified successfully for user: {}", email);

            return GoogleUserInfo.builder()
                    .email(email)
                    .firstName(givenName != null ? givenName : "")
                    .lastName(familyName != null ? familyName : "")
                    .fullName(fullName != null ? fullName : "")
                    .picture(pictureUrl)
                    .googleId(googleId)
                    .emailVerified(emailVerified)
                    .build();

        } catch (Exception e) {
            log.error("Error verifying Google token: {}", e.getMessage(), e);
            throw new UnauthorizedException("No se pudo verificar el token de Google: " + e.getMessage());
        }
    }
}
