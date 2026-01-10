package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByCommonAreaId(Long commonAreaId);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.commonAreaId = :commonAreaId " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((r.startTime <= :endTime AND r.endTime >= :startTime))")
    List<Reservation> findConflictingReservations(
            @Param("commonAreaId") Long commonAreaId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "AND r.startTime >= :now " +
           "ORDER BY r.startTime ASC")
    List<Reservation> findUpcomingReservationsByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);
}
