package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.model.sql.WhitelistContact;
import cl.lobbysync.backend.service.UserService;
import cl.lobbysync.backend.service.WhitelistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/whitelist")
@Tag(name = "Whitelist", description = "Gestion de lista blanca de visitas frecuentes")
@Slf4j
public class WhitelistController {

    @Autowired
    private WhitelistService whitelistService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Listar contactos de lista blanca", description = "Obtiene todos los contactos de la lista blanca del usuario autenticado")
    @GetMapping
    public ResponseEntity<List<WhitelistContact>> getWhitelist(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(whitelistService.getWhitelistByUnit(unitId));
    }

    @Operation(summary = "Crear contacto en lista blanca", description = "Agrega un nuevo contacto a la lista blanca")
    @PostMapping
    public ResponseEntity<WhitelistContact> createWhitelistContact(
            Authentication authentication,
            @RequestBody WhitelistContact contact) {
        
        User user = getUserFromAuth(authentication);
        Long unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        
        if (unitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        WhitelistContact created = whitelistService.createWhitelistContact(unitId, contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar contacto de lista blanca", description = "Modifica los datos de un contacto de lista blanca")
    @PutMapping("/{id}")
    public ResponseEntity<WhitelistContact> updateWhitelistContact(
            @PathVariable Long id,
            @RequestBody WhitelistContact contact) {
        
        WhitelistContact updated = whitelistService.updateWhitelistContact(id, contact);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar contacto de lista blanca", description = "Elimina un contacto de la lista blanca")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWhitelistContact(@PathVariable Long id) {
        whitelistService.deleteWhitelistContact(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        String firebaseUid = (String) authentication.getPrincipal();
        return userService.getUserByFirebaseUid(firebaseUid);
    }
}
