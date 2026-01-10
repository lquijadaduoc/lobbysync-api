package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.LoginRequest;
import cl.lobbysync.backend.dto.LoginResponse;
import cl.lobbysync.backend.dto.UserData;
import cl.lobbysync.backend.dto.UserSyncResponse;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.repository.UserRepository;
import cl.lobbysync.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Sincronizacion y verificacion de usuarios autenticados")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;

    private final Key jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Operation(
            summary = "Login simple",
            description = "Autentica un usuario por email y genera un token JWT"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail() : request.getUsername();
        
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(
                LoginResponse.builder()
                    .message("Email o username requerido")
                    .build()
            );
        }

        // Buscar usuario en la base de datos
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                LoginResponse.builder()
                    .message("Usuario no encontrado")
                    .build()
            );
        }

        User user = userOpt.get();
        
        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                LoginResponse.builder()
                    .message("Usuario inactivo")
                    .build()
            );
        }

        // Generar token JWT
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("userId", user.getId())
                .claim("firebaseUid", user.getFirebaseUid())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(jwtKey)
                .compact();

        // Construir objeto UserData
        UserData userData = UserData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .firebaseUid(user.getFirebaseUid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .build();

        return ResponseEntity.ok(
            LoginResponse.builder()
                .token(token)
                .user(userData)
                .message("Login exitoso")
                // Mantener campos legacy para compatibilidad con versiones antiguas
                .email(user.getEmail())
                .role(user.getRole())
                .userId(user.getId())
                .build()
        );
    }

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
