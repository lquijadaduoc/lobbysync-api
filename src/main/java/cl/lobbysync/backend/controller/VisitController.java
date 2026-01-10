package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.QrValidationRequest;
import cl.lobbysync.backend.dto.QrValidationResponse;
import cl.lobbysync.backend.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visits", description = "Validacion de visitas con codigo QR")
public class VisitController {

    @Autowired
    private InvitationService invitationService;

    @Operation(
            summary = "Validar codigo QR de visita",
            description = "El conserje escanea el QR y el sistema valida si es valido, si ya fue usado o si expiro."
    )
    @PostMapping("/validate")
    public ResponseEntity<QrValidationResponse> validateVisit(
            Authentication authentication,
            @RequestBody QrValidationRequest request) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            QrValidationResponse response = invitationService.validateQrToken(request.getQrToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            QrValidationResponse errorResponse = QrValidationResponse.builder()
                    .valid(false)
                    .message("Error al validar QR: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
