package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.CreateFirebaseUserRequest;
import cl.lobbysync.backend.service.FirebaseUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/firebase-users")
@Slf4j
@Tag(name = "Firebase Users", description = "Gestión de usuarios en Firebase Authentication")
public class FirebaseUserController {

    @Autowired
    private FirebaseUserService firebaseUserService;

    @Operation(
            summary = "Crear usuario en Firebase",
            description = "Crea un nuevo usuario directamente en Firebase Authentication y retorna su UID"
    )
    @PostMapping
    public ResponseEntity<Map<String, String>> createFirebaseUser(
            @RequestBody CreateFirebaseUserRequest request) {
        try {
            log.info("Creando usuario en Firebase: {}", request.getEmail());
            String firebaseUid = firebaseUserService.createFirebaseUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getDisplayName()
            );
            log.info("Usuario creado exitosamente. UID: {}", firebaseUid);
            return ResponseEntity.ok(Map.of(
                    "firebaseUid", firebaseUid,
                    "email", request.getEmail(),
                    "displayName", request.getDisplayName()
            ));
        } catch (Exception e) {
            log.error("Error creando usuario en Firebase: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Verificar usuario en Firebase",
            description = "Verifica si un usuario existe en Firebase por su email"
    )
    @GetMapping("/verify/{email}")
    public ResponseEntity<Map<String, Object>> verifyFirebaseUser(@PathVariable String email) {
        try {
            boolean exists = firebaseUserService.userExists(email);
            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "exists", exists
            ));
        } catch (Exception e) {
            log.error("Error verificando usuario: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Eliminar usuario de Firebase",
            description = "Elimina un usuario de Firebase Authentication por su UID"
    )
    @DeleteMapping("/{firebaseUid}")
    public ResponseEntity<Map<String, String>> deleteFirebaseUser(@PathVariable String firebaseUid) {
        try {
            log.info("Eliminando usuario de Firebase: {}", firebaseUid);
            firebaseUserService.deleteFirebaseUser(firebaseUid);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } catch (Exception e) {
            log.error("Error eliminando usuario: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
