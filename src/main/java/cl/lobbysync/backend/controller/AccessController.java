package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.AccessEntryRequest;
import cl.lobbysync.backend.dto.DeliveryEntryResponse;
import cl.lobbysync.backend.dto.QrEntryRequest;
import cl.lobbysync.backend.dto.QrEntryResponse;
import cl.lobbysync.backend.model.mongo.AccessLog;
import cl.lobbysync.backend.service.AccessControlService;
import cl.lobbysync.backend.service.AccessService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@Slf4j
public class AccessController {

    @Autowired
    private AccessService accessService;

    @Autowired
    private AccessControlService accessControlService;

    /**
     * POST /api/access/qr
     * Procesa entrada mediante código QR
     * 
     * @param request Objeto con el token del QR
     * @return 200 OK con los datos del invitado
     */
    @PostMapping("/qr")
    public ResponseEntity<QrEntryResponse> processQrEntry(@Valid @RequestBody QrEntryRequest request) {
        log.info("Received QR entry request with token: {}", request.getToken());
        
        try {
            QrEntryResponse response = accessControlService.processQrEntry(request.getToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid QR token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * POST /api/access/delivery
     * Procesa entrada de delivery con foto
     * 
     * @param photo Foto del paquete/delivery
     * @param unitId ID de la unidad de destino
     * @return 201 Created con la confirmación
     */
    @PostMapping(value = "/delivery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DeliveryEntryResponse> processDeliveryEntry(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("unitId") Long unitId) {
        
        log.info("Received delivery entry request for unit: {}", unitId);
        
        try {
            DeliveryEntryResponse response = accessControlService.processDeliveryEntry(unitId, photo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid delivery entry: {}", e.getMessage());
            throw e;
        }
    }

    // ==================== Endpoints Legacy ====================

    @PostMapping("/logs")
    public ResponseEntity<AccessLog> createAccessLog(
            Authentication authentication,
            @RequestBody AccessEntryRequest request) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        
        AccessLog accessLog = accessService.createAccessLog(
            userId,
            request.getAccessType(),
            request.getLocation(),
            request.getDescription()
        );
        
        return ResponseEntity.ok(accessLog);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AccessLog>> getAllAccessLogs() {
        return ResponseEntity.ok(accessService.getAllAccessLogs());
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AccessLog>> getUserAccessLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(accessService.getAccessLogsByUserId(userId));
    }

    @GetMapping("/logs/type/{accessType}")
    public ResponseEntity<List<AccessLog>> getAccessLogsByType(@PathVariable String accessType) {
        return ResponseEntity.ok(accessService.getAccessLogsByType(accessType));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // String firebaseUid = (String) authentication.getPrincipal();
            // TODO: Implementar búsqueda de userId por firebaseUid
            return 1L;
        }
        return 1L;
    }
}
