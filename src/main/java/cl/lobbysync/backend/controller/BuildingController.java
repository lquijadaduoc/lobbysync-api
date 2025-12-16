package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Building;
import cl.lobbysync.backend.service.BuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@Slf4j
public class BuildingController {

    @Autowired(required = false)
    private BuildingService buildingService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    @GetMapping
    public ResponseEntity<List<Building>> getAllBuildings() {
        try {
            if (buildingService == null) {
                return ResponseEntity.ok(List.of());
            }
            List<Building> buildings = buildingService.getAllBuildings();
            return ResponseEntity.ok(buildings);
        } catch (Exception e) {
            log.error("Error getting buildings", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Building>> getActiveBuildings() {
        return ResponseEntity.ok(buildingService.getActiveBuildings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    @PostMapping
    public ResponseEntity<Building> createBuilding(@RequestBody Building building) {
        return ResponseEntity.ok(buildingService.createBuilding(building));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Building> updateBuilding(
            @PathVariable Long id,
            @RequestBody Building buildingDetails) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, buildingDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
