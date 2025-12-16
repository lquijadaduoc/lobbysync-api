package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByBuildingId(Long buildingId);
    List<Unit> findByIsActive(Boolean isActive);
    List<Unit> findByOwnerId(Long ownerId);
}
