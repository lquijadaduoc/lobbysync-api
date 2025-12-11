package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Building;
import cl.lobbysync.backend.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
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
