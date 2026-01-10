package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.ParcelRequest;
import cl.lobbysync.backend.model.mongo.Parcel;
import cl.lobbysync.backend.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parcels")
@Tag(name = "Parcels", description = "Gestion de paquetes y entregas")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @Operation(
            summary = "Registrar paquete",
            description = "Crea un nuevo registro de paquete asociado al usuario autenticado."
    )
    @PostMapping
    public ResponseEntity<Parcel> createParcel(
            Authentication authentication,
            @RequestBody ParcelRequest request) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        
        Parcel parcel = parcelService.createParcel(
            userId,
            request.getTrackingNumber(),
            request.getCarrier(),
            request.getLocation(),
            request.getDescription()
        );
        
        return ResponseEntity.ok(parcel);
    }

    @Operation(
            summary = "Listar mis paquetes pendientes",
            description = "Obtiene los paquetes pendientes del usuario autenticado (RECEIVED, no DELIVERED)."
    )
    @GetMapping("/my-pending")
    public ResponseEntity<List<Parcel>> getMyPendingParcels(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<Parcel> pendingParcels = parcelService.getPendingParcelsByUserId(userId);
        return ResponseEntity.ok(pendingParcels);
    }

    @Operation(
            summary = "Listar paquetes de usuario",
            description = "Obtiene los paquetes asociados a un usuario por su ID."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Parcel>> getUserParcels(@PathVariable Long userId) {
        return ResponseEntity.ok(parcelService.getParcelsByUserId(userId));
    }

    @Operation(
            summary = "Obtener paquete por tracking",
            description = "Busca un paquete usando su numero de seguimiento."
    )
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Parcel> getParcelByTracking(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(parcelService.getParcelByTrackingNumber(trackingNumber));
    }

    @Operation(
            summary = "Listar paquetes por estado",
            description = "Filtra paquetes por estado (por ejemplo RECEIVED, DELIVERED)."
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Parcel>> getParcelsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(parcelService.getParcelsByStatus(status));
    }

    @Operation(
            summary = "Actualizar estado de paquete",
            description = "Actualiza el estado de un paquete especifico."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<Parcel> updateParcelStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(parcelService.updateParcelStatus(id, status));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String firebaseUid = (String) authentication.getPrincipal();
            return 1L;
        }
        return 1L;
    }
}
