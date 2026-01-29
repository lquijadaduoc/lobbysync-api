package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.*;
import cl.lobbysync.backend.exception.UnauthorizedException;
import cl.lobbysync.backend.exception.ValidationException;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.repository.UserRepository;
import cl.lobbysync.backend.service.GoogleAuthService;
import cl.lobbysync.backend.service.JwtKeyService;
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
@Tag(name = "Auth", description = "Autenticacion con Google OAuth y JWT")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;

    @Autowired
    private JwtKeyService jwtKeyService;

    @Operation(
            summary = "Login simple",
            description = "Autentica un usuario por email y genera un token JWT"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail() : request.getUsername();
        
        // Validación de email
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", 
                "El email es requerido. Proporciona un email válido en el campo 'email' o 'username'");
        }
        
        // Validación de formato de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("email", email, 
                "El formato del email es inválido. Ejemplo: usuario@dominio.com");
        }

        // Buscar usuario en la base de datos
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new UnauthorizedException(
                String.format("Usuario con email '%s' no encontrado. Verifica tus credenciales o regístrate primero.", email)
            );
        }

        User user = userOpt.get();
        
        if (!user.getIsActive()) {
            throw new UnauthorizedException(
                String.format("Usuario '%s' está inactivo. Contacta al administrador para reactivar tu cuenta.", email)
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
                .signWith(jwtKeyService.getKey())
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
                .unitId(user.getUnitId())
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
            String token = body.get("token");
            if (token == null || token.trim().isEmpty()) {
                throw new ValidationException("token", 
                    "El token de Firebase no puede estar vacío. Proporciona un ID token válido.");
            }
            
            try {
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
                firebaseUid = decodedToken.getUid();
            } catch (FirebaseAuthException e) {
                throw new cl.lobbysync.backend.exception.FirebaseException(
                    "Token de Firebase inválido o expirado: " + e.getMessage() + 
                    ". Asegúrate de proporcionar un ID token válido de Firebase Authentication."
                );
            }
        }
        
        if (firebaseUid == null) {
            throw new UnauthorizedException(
                "No se proporcionó autenticación. Envía un token JWT en el header Authorization o un token de Firebase en el body."
            );
        }
        
        try {
            UserSyncResponse response = userService.syncUserFromFirebase(firebaseUid);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            throw new cl.lobbysync.backend.exception.FirebaseException(
                "Error al sincronizar usuario desde Firebase: " + e.getMessage() + 
                ". Verifica que el usuario exista en Firebase Authentication."
            );
        }
    }

    @Operation(
            summary = "Login con Google",
            description = "Autentica un usuario con Google OAuth 2.0. " +
                    "El frontend debe enviar el Google ID Token obtenido desde Google Sign-In. " +
                    "El backend valida el token, busca/crea el usuario en PostgreSQL y retorna un JWT."
    )
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            // 1. Validar el Google ID Token
            GoogleUserInfo googleUser = googleAuthService.verifyGoogleToken(request.getIdToken());
            
            // 2. Buscar o crear usuario en PostgreSQL
            Optional<User> existingUser = userRepository.findByEmail(googleUser.getEmail());
            User user;
            
            if (existingUser.isEmpty()) {
                // Crear nuevo usuario desde Google
                user = new User();
                user.setEmail(googleUser.getEmail());
                user.setFirstName(googleUser.getFirstName());
                user.setLastName(googleUser.getLastName());
                user.setRole("RESIDENT"); // Rol por defecto
                user.setIsActive(true);
                user.setFirebaseUid(googleUser.getGoogleId()); // Guardamos el Google ID
                user = userRepository.save(user);
            } else {
                user = existingUser.get();
                
                // Actualizar información si cambió
                boolean updated = false;
                if (!googleUser.getFirstName().equals(user.getFirstName())) {
                    user.setFirstName(googleUser.getFirstName());
                    updated = true;
                }
                if (!googleUser.getLastName().equals(user.getLastName())) {
                    user.setLastName(googleUser.getLastName());
                    updated = true;
                }
                if (updated) {
                    user = userRepository.save(user);
                }
            }
            
            // Verificar que el usuario esté activo
            if (!user.getIsActive()) {
                throw new UnauthorizedException("Usuario desactivado. Contacta al administrador.");
            }
            
            // 3. Generar JWT custom
            Key key = jwtKeyService.getSigningKey();
            String jwtToken = Jwts.builder()
                    .subject(user.getEmail())
                    .claim("userId", user.getId())
                    .claim("role", user.getRole())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                    .signWith(key, Jwts.SIG.HS256)
                    .compact();
            
            // 4. Preparar respuesta
            UserData userData = new UserData();
            userData.setId(user.getId());
            userData.setEmail(user.getEmail());
            userData.setFirstName(user.getFirstName());
            userData.setLastName(user.getLastName());
            userData.setRole(user.getRole());
            userData.setUnitId(user.getUnitId());
            
            LoginResponse response = new LoginResponse();
            response.setToken(jwtToken);
            response.setUser(userData);
            response.setMessage("Login con Google exitoso");
            
            return ResponseEntity.ok(response);
            
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Error al procesar login con Google: " + e.getMessage());
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
                "message", "Token válido y autenticado correctamente"
            ));
        }
        throw new UnauthorizedException(
            "Token inválido o expirado. Proporciona un token válido en el header Authorization: Bearer <token>"
        );
    }
}
