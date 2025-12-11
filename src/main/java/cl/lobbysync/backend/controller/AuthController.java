package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.UserSyncResponse;
import cl.lobbysync.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private FirebaseAuth firebaseAuth;

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
            return ResponseEntity.unauthorized().build();
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
