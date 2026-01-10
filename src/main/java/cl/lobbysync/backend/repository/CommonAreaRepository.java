package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.CommonArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonAreaRepository extends JpaRepository<CommonArea, Long> {
    List<CommonArea> findByIsActiveTrue();
    List<CommonArea> findByBuildingId(Long buildingId);
    List<CommonArea> findByBuildingIdAndIsActiveTrue(Long buildingId);
}
