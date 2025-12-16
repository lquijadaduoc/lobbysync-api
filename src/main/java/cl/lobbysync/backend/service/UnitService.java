package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.repository.UnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UnitService {

    @Autowired
    private UnitRepository unitRepository;

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    public List<Unit> getUnitsByBuildingId(Long buildingId) {
        return unitRepository.findByBuildingId(buildingId);
    }

    public Unit getUnitById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    public Unit updateUnit(Long id, Unit unitDetails) {
        Unit unit = getUnitById(id);
        unit.setUnitNumber(unitDetails.getUnitNumber());
        unit.setBuildingId(unitDetails.getBuildingId());
        unit.setAliquot(unitDetails.getAliquot());
        unit.setOwnerId(unitDetails.getOwnerId());
        unit.setIsActive(unitDetails.getIsActive());
        unit.setFloor(unitDetails.getFloor());
        unit.setSurfaceArea(unitDetails.getSurfaceArea());
        return unitRepository.save(unit);
    }

    public void deleteUnit(Long id) {
        unitRepository.deleteById(id);
    }
}
