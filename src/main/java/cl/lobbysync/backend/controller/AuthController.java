package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.UserSyncResponse;
import cl.lobbysync.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Sincronizacion y verificacion de usuarios autenticados")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Operation(
            summary = "Sincronizar usuario",
            description = "Sincroniza un usuario desde Firebase usando el token JWT o el principal autenticado."
    )
    @PostMapping("/sync-user")
    public ResponseEntity<UserSyncResponse> syncUser(
            Authentication authentication,
            @RequestBody(required = false) Map<String, String> body) {
        
        String firebaseUid = null;
        
        if (authentication != null && authentication.isAuthenticated()) {
            firebaseUid = (String) authentication.getPrincipal();
        } else if (body != null && body.containsKey("token")) {
            try {
                String token = body.get("token");
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
                firebaseUid = decodedToken.getUid();
            } catch (FirebaseAuthException e) {
                return ResponseEntity.badRequest().body(
                    UserSyncResponse.builder()
                        .message("Invalid Firebase token: " + e.getMessage())
                        .build()
                );
            }
        }
        
        if (firebaseUid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            UserSyncResponse response = userService.syncUserFromFirebase(firebaseUid);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                UserSyncResponse.builder()
                    .message("Error syncing user: " + e.getMessage())
                    .build()
            );
        }
    }

    @Operation(
            summary = "Verificar token",
            description = "Verifica si el principal autenticado es valido y retorna su UID."
    )
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "uid", authentication.getPrincipal(),
                "message", "Token is valid"
            ));
        }
        return ResponseEntity.status(401).body(Map.of(
            "authenticated", false,
            "message", "Token is invalid"
        ));
    }
}
