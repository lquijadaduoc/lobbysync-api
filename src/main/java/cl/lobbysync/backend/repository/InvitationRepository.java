package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Invitation;
import cl.lobbysync.backend.model.sql.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByQrToken(String qrToken);
    List<Invitation> findByStatus(InvitationStatus status);
    List<Invitation> findByUnitId(Long unitId);
    List<Invitation> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
}
