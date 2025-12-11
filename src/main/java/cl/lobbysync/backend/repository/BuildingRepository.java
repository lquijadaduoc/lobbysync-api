package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByIsActive(Boolean isActive);
}
