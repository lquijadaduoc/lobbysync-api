package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.ChangePasswordRequest;
import cl.lobbysync.backend.dto.CreateUserRequest;
import cl.lobbysync.backend.dto.UpdateUserRequest;
import cl.lobbysync.backend.dto.UserCreationResponse;
import cl.lobbysync.backend.exception.ValidationException;
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
        if (id == null || id <= 0) {
            throw new ValidationException("id", id, "El ID del usuario debe ser un número positivo");
        }
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Obtener usuario por email",
            description = "Busca un usuario por su email registrado."
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "El email no puede estar vacío");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("email", email, "El formato del email es inválido. Ejemplo: usuario@dominio.com");
        }
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
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
        // Validaciones adicionales de negocio
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("email", "El email es requerido");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ValidationException("password", "La contraseña debe tener al menos 6 caracteres");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new ValidationException("role", "El rol es requerido (ADMIN, CONCIERGE, RESIDENT, FINANCE)");
        }
        
        // Validar que el rol sea válido
        String[] validRoles = {"ADMIN", "CONCIERGE", "RESIDENT", "FINANCE"};
        boolean validRole = false;
        for (String role : validRoles) {
            if (role.equalsIgnoreCase(request.getRole())) {
                validRole = true;
                break;
            }
        }
        if (!validRole) {
            throw new ValidationException("role", request.getRole(), 
                "El rol debe ser uno de: ADMIN, CONCIERGE, RESIDENT, FINANCE");
        }
        
        log.info("Creating new user with email: {}", request.getEmail());
        UserCreationResponse response = userService.createUserWithFirebase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        if (id == null || id <= 0) {
            throw new ValidationException("id", id, "El ID del usuario debe ser un número positivo");
        }
        
        // Validar rol si se proporciona
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            String[] validRoles = {"ADMIN", "CONCIERGE", "RESIDENT", "FINANCE"};
            boolean validRole = false;
            for (String role : validRoles) {
                if (role.equalsIgnoreCase(request.getRole())) {
                    validRole = true;
                    break;
                }
            }
            if (!validRole) {
                throw new ValidationException("role", request.getRole(), 
                    "El rol debe ser uno de: ADMIN, CONCIERGE, RESIDENT, FINANCE");
            }
        }
        
        log.info("Updating user with ID: {}", id);
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
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
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("id", id, "El ID del usuario debe ser un número positivo");
        }
        
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of(
            "message", "Usuario eliminado exitosamente",
            "userId", id.toString()
        ));
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
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        if (id == null || id <= 0) {
            throw new ValidationException("id", id, "El ID del usuario debe ser un número positivo");
        }
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new ValidationException("newPassword", "La nueva contraseña es requerida");
        }
        if (request.getNewPassword().length() < 6) {
            throw new ValidationException("newPassword", 
                "La contraseña debe tener al menos 6 caracteres para cumplir con los requisitos de Firebase");
        }
        
        log.info("Changing password for user ID: {}", id);
        userService.changePassword(id, request.getNewPassword());
        return ResponseEntity.ok(Map.of(
            "message", "Contraseña cambiada exitosamente en Firebase",
            "userId", id.toString()
        ));
    }
}
