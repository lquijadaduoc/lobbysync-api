package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.Building;
import cl.lobbysync.backend.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    public List<Building> getActiveBuildings() {
        return buildingRepository.findByIsActive(true);
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found"));
    }

    public Building createBuilding(Building building) {
        return buildingRepository.save(building);
    }

    public Building updateBuilding(Long id, Building buildingDetails) {
        Building building = getBuildingById(id);
        building.setName(buildingDetails.getName());
        building.setAddress(buildingDetails.getAddress());
        building.setIsActive(buildingDetails.getIsActive());
        return buildingRepository.save(building);
    }

    public void deleteBuilding(Long id) {
        buildingRepository.deleteById(id);
    }
}
