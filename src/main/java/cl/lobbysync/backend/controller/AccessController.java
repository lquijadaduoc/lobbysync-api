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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Access", description = "Endpoints de control de acceso y registro")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @Autowired
    private AccessControlService accessControlService;

    /**
     * POST /api/access/qr
     * Procesa entrada mediante cИdigo QR
     * 
     * @param request Objeto con el token del QR
     * @return 200 OK con los datos del invitado
     */
    @Operation(
            summary = "Procesar entrada por QR",
            description = "Valida el token QR recibido y retorna los datos del invitado autorizado."
    )
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
     * @return 201 Created con la confirmaciИn
     */
    @Operation(
            summary = "Registrar ingreso de delivery",
            description = "Recibe foto y unidad destino para registrar ingreso de delivery y generar aviso."
    )
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

    @Operation(
            summary = "Crear registro de acceso",
            description = "Crea un registro manual de acceso con tipo, ubicacion y descripcion."
    )
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

    @Operation(
            summary = "Listar registros de acceso",
            description = "Retorna todos los registros de acceso almacenados."
    )
    @GetMapping("/logs")
    public ResponseEntity<List<AccessLog>> getAllAccessLogs() {
        return ResponseEntity.ok(accessService.getAllAccessLogs());
    }

    @Operation(
            summary = "Registros de acceso por usuario",
            description = "Obtiene registros de acceso filtrados por ID de usuario."
    )
    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AccessLog>> getUserAccessLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(accessService.getAccessLogsByUserId(userId));
    }

    @Operation(
            summary = "Registros de acceso por tipo",
            description = "Filtra los registros de acceso por tipo (por ejemplo ENTRY, EXIT, DELIVERY)."
    )
    @GetMapping("/logs/type/{accessType}")
    public ResponseEntity<List<AccessLog>> getAccessLogsByType(@PathVariable String accessType) {
        return ResponseEntity.ok(accessService.getAccessLogsByType(accessType));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // String firebaseUid = (String) authentication.getPrincipal();
            // TODO: Implementar bカsqueda de userId por firebaseUid
            return 1L;
        }
        return 1L;
    }
}
