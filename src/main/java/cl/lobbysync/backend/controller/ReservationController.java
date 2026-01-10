package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.ReservationRequest;
import cl.lobbysync.backend.model.sql.CommonArea;
import cl.lobbysync.backend.model.sql.Reservation;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.service.ReservationService;
import cl.lobbysync.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Gestion de reservas de areas comunes")
@Slf4j
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Listar areas comunes disponibles",
            description = "Retorna todas las areas comunes activas disponibles para reservar."
    )
    @GetMapping("/common-areas")
    public ResponseEntity<List<CommonArea>> getCommonAreas(
            @RequestParam(required = false) Long buildingId) {
        
        if (buildingId != null) {
            return ResponseEntity.ok(reservationService.getCommonAreasByBuilding(buildingId));
        }
        
        return ResponseEntity.ok(reservationService.getAllActiveCommonAreas());
    }

    @Operation(
            summary = "Crear reserva de area comun",
            description = "Crea una nueva reserva para el usuario autenticado en un area comun especifica."
    )
    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            Authentication authentication,
            @RequestBody ReservationRequest request) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        Reservation reservation = reservationService.createReservation(
                user.getId(),
                request.getCommonAreaId(),
                request.getUnitId(),
                request.getStartTime(),
                request.getEndTime(),
                request.getNotes()
        );
        
        return ResponseEntity.ok(reservation);
    }

    @Operation(
            summary = "Listar mis reservas proximas",
            description = "Retorna las reservas futuras del usuario autenticado."
    )
    @GetMapping("/my-upcoming")
    public ResponseEntity<List<Reservation>> getMyUpcomingReservations(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        List<Reservation> reservations = reservationService.getUpcomingReservationsByUserId(user.getId());
        return ResponseEntity.ok(reservations);
    }

    @Operation(
            summary = "Listar todas mis reservas",
            description = "Retorna todas las reservas del usuario autenticado (pasadas y futuras)."
    )
    @GetMapping("/my-reservations")
    public ResponseEntity<List<Reservation>> getMyReservations(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        List<Reservation> reservations = reservationService.getAllReservationsByUserId(user.getId());
        return ResponseEntity.ok(reservations);
    }

    @Operation(
            summary = "Cancelar reserva",
            description = "Cancela una reserva existente del usuario autenticado."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            Authentication authentication,
            @PathVariable Long id) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        reservationService.cancelReservation(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Actualizar estado de reserva",
            description = "Actualiza el estado de una reserva (PENDING, CONFIRMED, CANCELLED, COMPLETED)."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        Reservation.ReservationStatus newStatus = Reservation.ReservationStatus.valueOf(status.toUpperCase());
        Reservation updated = reservationService.updateReservationStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }
}
