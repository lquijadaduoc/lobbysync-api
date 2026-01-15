package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.ReservationGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationGuestRepository extends JpaRepository<ReservationGuest, Long> {
    List<ReservationGuest> findByReservationId(Long reservationId);
    void deleteByReservationId(Long reservationId);
}
