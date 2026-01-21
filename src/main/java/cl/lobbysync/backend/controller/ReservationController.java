package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.CheckOutRequest;
import cl.lobbysync.backend.dto.ReservationApprovalRequest;
import cl.lobbysync.backend.dto.ReservationGuestRequest;
import cl.lobbysync.backend.dto.ReservationRequest;
import cl.lobbysync.backend.exception.UnauthorizedException;
import cl.lobbysync.backend.model.sql.CommonArea;
import cl.lobbysync.backend.model.sql.Reservation;
import cl.lobbysync.backend.model.sql.ReservationGuest;
import cl.lobbysync.backend.model.sql.User;
import cl.lobbysync.backend.service.ReservationService;
import cl.lobbysync.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
        
        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        reservationService.cancelReservation(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Actualizar estado de reserva",
            description = "Actualiza el estado de una reserva (PENDING, APPROVED, REJECTED, IN_USE, COMPLETED, CANCELLED)."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        Reservation.ReservationStatus newStatus = Reservation.ReservationStatus.valueOf(status.toUpperCase());
        Reservation updated = reservationService.updateReservationStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    // ===== NUEVOS ENDPOINTS PARA FLUJO COMPLETO =====

    @Operation(
            summary = "Aprobar o rechazar reserva",
            description = "ADMIN: Aprueba o rechaza una reserva pendiente."
    )
    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveOrRejectReservation(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody ReservationApprovalRequest request) {
        
        String firebaseUid = (String) authentication.getPrincipal();
        User admin = userService.getUserByFirebaseUid(firebaseUid);
        
        Reservation updated = reservationService.approveOrRejectReservation(
                id, admin.getId(), request.getApproved(), request.getRejectionReason());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Check-in de reserva",
            description = "CONSERJE: Marca el check-in cuando el residente recoge las llaves."
    )
    @PostMapping("/{id}/check-in")
    public ResponseEntity<Reservation> checkIn(@PathVariable Long id) {
        Reservation updated = reservationService.checkIn(id);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Check-out de reserva",
            description = "CONSERJE: Marca el check-out cuando el residente devuelve las llaves."
    )
    @PostMapping("/{id}/check-out")
    public ResponseEntity<Reservation> checkOut(
            @PathVariable Long id,
            @RequestBody(required = false) CheckOutRequest request) {
        
        Boolean requiresCleaning = request != null ? request.getRequiresCleaning() : false;
        String cleaningNotes = request != null ? request.getCleaningNotes() : null;
        
        Reservation updated = reservationService.checkOut(id, requiresCleaning, cleaningNotes);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Agregar invitados a reserva",
            description = "RESIDENTE: Agrega lista de invitados a su reserva aprobada."
    )
    @PostMapping("/{id}/guests")
    public ResponseEntity<List<ReservationGuest>> addGuests(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody List<ReservationGuestRequest> guests) {
        
        String firebaseUid = (String) authentication.getPrincipal();
        User user = userService.getUserByFirebaseUid(firebaseUid);
        
        List<ReservationGuest> addedGuests = reservationService.addGuestsToReservation(id, user.getId(), guests);
        return ResponseEntity.ok(addedGuests);
    }

    @Operation(
            summary = "Listar invitados de reserva",
            description = "Obtiene la lista de invitados de una reserva."
    )
    @GetMapping("/{id}/guests")
    public ResponseEntity<List<ReservationGuest>> getReservationGuests(@PathVariable Long id) {
        List<ReservationGuest> guests = reservationService.getReservationGuests(id);
        return ResponseEntity.ok(guests);
    }

    @Operation(
            summary = "Listar todas las reservas (Admin/Conserje)",
            description = "Obtiene todas las reservas del sistema para gestión."
    )
    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long buildingId) {
        
        List<Reservation> reservations = reservationService.getAllReservations(status, buildingId);
        return ResponseEntity.ok(reservations);
    }

    @Operation(
            summary = "Obtener disponibilidad de área común",
            description = "Verifica qué horarios están disponibles para reservar en una fecha específica."
    )
    @GetMapping("/availability/{commonAreaId}")
    public ResponseEntity<?> getAvailability(
            @PathVariable Long commonAreaId,
            @RequestParam String date) {
        
        var availability = reservationService.getAvailabilityForDate(commonAreaId, date);
        return ResponseEntity.ok(availability);
    }
}
