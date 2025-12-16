package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Invitation;
import cl.lobbysync.backend.service.InvitationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@Slf4j
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    /**
     * POST /api/invitations
     * Genera un nuevo token QR para que el residente lo comparta
     */
    @PostMapping
    public ResponseEntity<Invitation> createInvitation(@Valid @RequestBody Invitation invitation) {
        log.info("Creating invitation for guest: {}", invitation.getGuestName());
        Invitation created = invitationService.createInvitation(invitation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Invitation>> getAllInvitations(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String status) {
        
        if (unitId != null) {
            return ResponseEntity.ok(invitationService.getInvitationsByUnitId(unitId));
        }
        
        return ResponseEntity.ok(invitationService.getAllInvitations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invitation> getInvitationById(@PathVariable Long id) {
        return ResponseEntity.ok(invitationService.getInvitationById(id));
    }

    @GetMapping("/qr/{qrToken}")
    public ResponseEntity<Invitation> getInvitationByQrToken(@PathVariable String qrToken) {
        return ResponseEntity.ok(invitationService.getInvitationByQrToken(qrToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Long id) {
        invitationService.deleteInvitation(id);
        return ResponseEntity.noContent().build();
    }
}
