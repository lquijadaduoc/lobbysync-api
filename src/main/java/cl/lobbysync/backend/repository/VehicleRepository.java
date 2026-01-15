package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUnitId(Long unitId);
    List<Vehicle> findByUnitIdAndIsActive(Long unitId, Boolean isActive);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    void deleteByUnitId(Long unitId);
}
