package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.CommonArea;
import cl.lobbysync.backend.model.sql.Reservation;
import cl.lobbysync.backend.repository.CommonAreaRepository;
import cl.lobbysync.backend.repository.ReservationRepository;
import cl.lobbysync.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CommonAreaRepository commonAreaRepository;

    @Autowired
    private UserRepository userRepository;

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
}
