package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.Building;
import cl.lobbysync.backend.repository.BuildingRepository;
import cl.lobbysync.backend.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UnitRepository unitRepository;

    public List<Building> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        // Calcular totalUnits para cada edificio
        buildings.forEach(building -> {
            int unitCount = unitRepository.countByBuildingId(building.getId());
            building.setTotalUnits(unitCount);
        });
        return buildings;
    }

    public List<Building> getActiveBuildings() {
        List<Building> buildings = buildingRepository.findByIsActive(true);
        buildings.forEach(building -> {
            int unitCount = unitRepository.countByBuildingId(building.getId());
            building.setTotalUnits(unitCount);
        });
        return buildings;
    }

    public Building getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found"));
        int unitCount = unitRepository.countByBuildingId(building.getId());
        building.setTotalUnits(unitCount);
        return building;
    }

    public Building createBuilding(Building building) {
        return buildingRepository.save(building);
    }

    public Building updateBuilding(Long id, Building buildingDetails) {
        Building building = getBuildingById(id);
        building.setName(buildingDetails.getName());
        building.setAddress(buildingDetails.getAddress());
        building.setFloors(buildingDetails.getFloors());
        building.setIsActive(buildingDetails.getIsActive());
        return buildingRepository.save(building);
    }

    public void deleteBuilding(Long id) {
        buildingRepository.deleteById(id);
    }
}
