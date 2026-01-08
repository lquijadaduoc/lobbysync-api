package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Tag(name = "Users", description = "Consulta de usuarios sincronizados")
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

        String firebaseUid = (String) authentication.getPrincipal();
        log.info("Getting current user info for: {}", firebaseUid);
        
        User user = userService.getUserByFirebaseUid(firebaseUid);
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
}
