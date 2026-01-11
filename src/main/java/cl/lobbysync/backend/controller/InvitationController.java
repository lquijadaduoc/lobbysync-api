package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Invitation;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.service.InvitationService;
import cl.lobbysync.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@Slf4j
@Tag(name = "Invitations", description = "Gestion de invitaciones y codigos QR")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserService userService;

    /**
     * POST /api/invitations
     * Genera un nuevo token QR para que el residente lo comparta
     */
    @Operation(
            summary = "Crear invitacion",
            description = "Genera una invitacion con QR para compartir con visitantes."
    )
    @PostMapping
    public ResponseEntity<Invitation> createInvitation(
            Authentication authentication,
            @Valid @RequestBody Invitation invitation) {
        
        if (authentication != null && authentication.isAuthenticated()) {
            String firebaseUid = (String) authentication.getPrincipal();
            User user = userService.getUserByFirebaseUid(firebaseUid);
            invitation.setCreatedBy(user.getId());
        }
        
        log.info("Creating invitation for guest: {}", invitation.getGuestName());
        Invitation created = invitationService.createInvitation(invitation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Listar mis invitaciones",
            description = "Lista todas las invitaciones creadas por el usuario autenticado, ordenadas por fecha de creacion."
    )
    @GetMapping("/my-invitations")
    public ResponseEntity<List<Invitation>> getMyInvitations(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        List<Invitation> invitations = invitationService.getInvitationsByCreatedBy(user.getId());
        return ResponseEntity.ok(invitations);
    }

    @Operation(
            summary = "Listar invitaciones",
            description = "Lista todas las invitaciones; permite filtrar por unidad o estado."
    )
    @GetMapping
    public ResponseEntity<List<Invitation>> getAllInvitations(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String status) {
        
        if (unitId != null) {
            return ResponseEntity.ok(invitationService.getInvitationsByUnitId(unitId));
        }
        
        return ResponseEntity.ok(invitationService.getAllInvitations());
    }

    @Operation(
            summary = "Obtener invitacion",
            description = "Recupera una invitacion especifica por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Invitation> getInvitationById(@PathVariable Long id) {
        return ResponseEntity.ok(invitationService.getInvitationById(id));
    }

    @Operation(
            summary = "Buscar invitacion por token QR",
            description = "Busca una invitacion usando el token QR generado."
    )
    @GetMapping("/qr/{qrToken}")
    public ResponseEntity<Invitation> getInvitationByQrToken(@PathVariable String qrToken) {
        return ResponseEntity.ok(invitationService.getInvitationByQrToken(qrToken));
    }

    @Operation(
            summary = "Eliminar invitacion",
            description = "Elimina una invitacion por su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Long id) {
        invitationService.deleteInvitation(id);
        return ResponseEntity.noContent().build();
    }
}
