package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.ChangePasswordRequest;
import cl.lobbysync.backend.dto.CreateUserRequest;
import cl.lobbysync.backend.dto.UpdateUserRequest;
import cl.lobbysync.backend.dto.UserCreationResponse;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Tag(name = "Users", description = "Gestión completa de usuarios con Firebase")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/v1/users
     * Lista todos los usuarios del sistema
     */
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Retorna la lista completa de usuarios registrados en el sistema."
    )
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Listing all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/me
     * Devuelve la informaciИn y rol del usuario logueado
     */
    @Operation(
            summary = "Obtener usuario actual",
            description = "Devuelve informacion del usuario autenticado a partir del token JWT."
    )
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String principal = (String) authentication.getPrincipal();
        log.info("Getting current user info for principal: {}", principal);
        
        // El principal puede ser email (JWT backend) o firebase_uid (Firebase token)
        User user;
        if (principal.contains("@")) {
            // Es un email (viene del JWT del backend)
            user = userService.getUserByEmail(principal);
        } else {
            // Es un firebase_uid (viene de Firebase)
            user = userService.getUserByFirebaseUid(principal);
        }
        
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Recupera un usuario especifico por su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
            summary = "Obtener usuario por email",
            description = "Busca un usuario por su email registrado."
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * POST /api/v1/users
     * Crea un nuevo usuario en Firebase y PostgreSQL
     */
    @Operation(
            summary = "Crear nuevo usuario",
            description = "Crea un usuario en Firebase Authentication y lo sincroniza con PostgreSQL."
    )
    @PostMapping
    public ResponseEntity<UserCreationResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            log.info("Creating new user with email: {}", request.getEmail());
            UserCreationResponse response = userService.createUserWithFirebase(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                UserCreationResponse.builder()
                    .success(false)
                    .message("Error al crear usuario: " + e.getMessage())
                    .build()
            );
        }
    }

    /**
     * PUT /api/v1/users/{id}
     * Actualiza un usuario existente
     */
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza la información de un usuario existente en PostgreSQL y Firebase."
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            log.info("Updating user with ID: {}", id);
            User updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/v1/users/{id}
     * Elimina un usuario de Firebase y PostgreSQL
     */
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario de Firebase Authentication y PostgreSQL."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            log.info("Deleting user with ID: {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar usuario: " + e.getMessage()));
        }
    }

    /**
     * POST /api/v1/users/{id}/change-password
     * Cambia la contraseña de un usuario en Firebase
     */
    @Operation(
            summary = "Cambiar contraseña",
            description = "Cambia la contraseña de un usuario en Firebase Authentication."
    )
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            log.info("Changing password for user ID: {}", id);
            userService.changePassword(id, request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña cambiada exitosamente"));
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar contraseña: " + e.getMessage()));
        }
    }
}
