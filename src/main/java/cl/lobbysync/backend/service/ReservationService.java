package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.ReservationGuestRequest;
import cl.lobbysync.backend.model.sql.CommonArea;
import cl.lobbysync.backend.model.sql.Reservation;
import cl.lobbysync.backend.model.sql.ReservationGuest;
import cl.lobbysync.backend.repository.CommonAreaRepository;
import cl.lobbysync.backend.repository.ReservationGuestRepository;
import cl.lobbysync.backend.repository.ReservationRepository;
import cl.lobbysync.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CommonAreaRepository commonAreaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationGuestRepository reservationGuestRepository;

    public List<CommonArea> getAllActiveCommonAreas() {
        return commonAreaRepository.findByIsActiveTrue();
    }

    public List<CommonArea> getCommonAreasByBuilding(Long buildingId) {
        return commonAreaRepository.findByBuildingIdAndIsActiveTrue(buildingId);
    }

    public CommonArea getCommonAreaById(Long id) {
        return commonAreaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Area comun no encontrada"));
    }

    public Reservation createReservation(Long userId, Long commonAreaId, Long unitId, 
                                        LocalDateTime startTime, LocalDateTime endTime, 
                                        String notes) {
        
        // Validar que el area comun exista
        CommonArea commonArea = getCommonAreaById(commonAreaId);
        
        // Validar que el usuario exista
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Validar que no haya conflictos de horario
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                commonAreaId, startTime, endTime);
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("El area comun ya esta reservada en ese horario");
        }
        
        // Calcular monto total
        Double totalAmount = calculateTotalAmount(commonArea, startTime, endTime);
        
        Reservation reservation = Reservation.builder()
                .commonAreaId(commonAreaId)
                .userId(userId)
                .unitId(unitId)
                .startTime(startTime)
                .endTime(endTime)
                .notes(notes)
                .totalAmount(totalAmount)
                .status(Reservation.ReservationStatus.PENDING)
                .build();
        
        log.info("Creating reservation for user {} at common area {} from {} to {}", 
                userId, commonAreaId, startTime, endTime);
        
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getUpcomingReservationsByUserId(Long userId) {
        return reservationRepository.findUpcomingReservationsByUserId(
                userId, LocalDateTime.now());
    }

    public List<Reservation> getAllReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservation updateReservationStatus(Long reservationId, Reservation.ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private Double calculateTotalAmount(CommonArea commonArea, LocalDateTime startTime, LocalDateTime endTime) {
        if (commonArea.getHourlyRate() == null || commonArea.getHourlyRate() == 0) {
            return 0.0;
        }
        
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        
        return commonArea.getHourlyRate() * hours;
    }

    // ===== NUEVOS MÉTODOS PARA FLUJO COMPLETO =====

    public Reservation approveOrRejectReservation(Long reservationId, Long adminId, 
                                                  Boolean approved, String rejectionReason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (!reservation.getStatus().equals(Reservation.ReservationStatus.PENDING)) {
            throw new RuntimeException("Solo se pueden aprobar/rechazar reservas pendientes");
        }
        
        if (approved) {
            reservation.setStatus(Reservation.ReservationStatus.APPROVED);
            reservation.setApprovedBy(adminId);
            reservation.setApprovedAt(LocalDateTime.now());
            log.info("Reservation {} approved by admin {}", reservationId, adminId);
        } else {
            reservation.setStatus(Reservation.ReservationStatus.REJECTED);
            reservation.setRejectionReason(rejectionReason);
            log.info("Reservation {} rejected by admin {}: {}", reservationId, adminId, rejectionReason);
        }
        
        return reservationRepository.save(reservation);
    }

    public Reservation checkIn(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (!reservation.getStatus().equals(Reservation.ReservationStatus.APPROVED)) {
            throw new RuntimeException("Solo se puede hacer check-in en reservas aprobadas");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.IN_USE);
        reservation.setCheckInTime(LocalDateTime.now());
        
        log.info("Check-in completed for reservation {}", reservationId);
        return reservationRepository.save(reservation);
    }

    public Reservation checkOut(Long reservationId, Boolean requiresCleaning, String cleaningNotes) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (!reservation.getStatus().equals(Reservation.ReservationStatus.IN_USE)) {
            throw new RuntimeException("Solo se puede hacer check-out en reservas en uso");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        reservation.setCheckOutTime(LocalDateTime.now());
        reservation.setRequiresCleaning(requiresCleaning);
        reservation.setCleaningNotes(cleaningNotes);
        
        log.info("Check-out completed for reservation {}. Cleaning required: {}", 
                reservationId, requiresCleaning);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public List<ReservationGuest> addGuestsToReservation(Long reservationId, Long userId, 
                                                         List<ReservationGuestRequest> guestRequests) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("Solo el dueño de la reserva puede agregar invitados");
        }
        
        if (!reservation.getStatus().equals(Reservation.ReservationStatus.APPROVED)) {
            throw new RuntimeException("Solo se pueden agregar invitados a reservas aprobadas");
        }
        
        List<ReservationGuest> guests = new ArrayList<>();
        for (ReservationGuestRequest request : guestRequests) {
            ReservationGuest guest = ReservationGuest.builder()
                    .reservationId(reservationId)
                    .name(request.getName())
                    .rut(request.getRut())
                    .phone(request.getPhone())
                    .checkedIn(false)
                    .build();
            guests.add(reservationGuestRepository.save(guest));
        }
        
        log.info("Added {} guests to reservation {}", guests.size(), reservationId);
        return guests;
    }

    public List<ReservationGuest> getReservationGuests(Long reservationId) {
        return reservationGuestRepository.findByReservationId(reservationId);
    }

    public List<Reservation> getAllReservations(String status, Long buildingId) {
        if (status != null && buildingId != null) {
            Reservation.ReservationStatus reservationStatus = Reservation.ReservationStatus.valueOf(status.toUpperCase());
            return reservationRepository.findByStatusAndCommonAreaBuildingId(reservationStatus, buildingId);
        } else if (status != null) {
            Reservation.ReservationStatus reservationStatus = Reservation.ReservationStatus.valueOf(status.toUpperCase());
            return reservationRepository.findByStatus(reservationStatus);
        } else if (buildingId != null) {
            return reservationRepository.findByCommonAreaBuildingId(buildingId);
        }
        return reservationRepository.findAll();
    }

    public Map<String, Object> getAvailabilityForDate(Long commonAreaId, String date) {
        CommonArea commonArea = getCommonAreaById(commonAreaId);
        
        // Obtener reservas del día
        LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");
        
        List<Reservation> reservations = reservationRepository.findByCommonAreaIdAndDateRange(
                commonAreaId, startOfDay, endOfDay);
        
        Map<String, Object> response = new HashMap<>();
        response.put("commonArea", commonArea);
        response.put("date", date);
        response.put("reservations", reservations);
        response.put("availableSlots", calculateAvailableSlots(reservations, startOfDay, endOfDay));
        
        return response;
    }

    private List<Map<String, String>> calculateAvailableSlots(List<Reservation> reservations, 
                                                              LocalDateTime start, LocalDateTime end) {
        List<Map<String, String>> slots = new ArrayList<>();
        // Implementación simplificada - puedes mejorarla según los bloques horarios del área
        // Por ahora retorna un array vacío si hay reservas, o un slot completo si está libre
        if (reservations.isEmpty()) {
            Map<String, String> slot = new HashMap<>();
            slot.put("start", start.toString());
            slot.put("end", end.toString());
            slots.add(slot);
        }
        return slots;
    }
}
