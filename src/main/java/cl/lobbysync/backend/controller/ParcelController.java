package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.ParcelRequest;
import cl.lobbysync.backend.model.mongo.Parcel;
import cl.lobbysync.backend.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Parcel>> getUserParcels(@PathVariable Long userId) {
        return ResponseEntity.ok(parcelService.getParcelsByUserId(userId));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Parcel> getParcelByTracking(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(parcelService.getParcelByTrackingNumber(trackingNumber));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Parcel>> getParcelsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(parcelService.getParcelsByStatus(status));
    }

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
