package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Building;
import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.service.BuildingService;
import cl.lobbysync.backend.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings")
@Slf4j
@Tag(name = "Buildings", description = "Gestion de edificios y sus unidades")
public class BuildingController {

    @Autowired(required = false)
    private BuildingService buildingService;

    @Autowired(required = false)
    private UnitService unitService;

    @Operation(
            summary = "Listar edificios",
            description = "Retorna todos los edificios registrados."
    )
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

    @Operation(
            summary = "Listar edificios activos",
            description = "Devuelve los edificios con estado activo."
    )
    @GetMapping("/active")
    public ResponseEntity<List<Building>> getActiveBuildings() {
        return ResponseEntity.ok(buildingService.getActiveBuildings());
    }

    @Operation(
            summary = "Obtener edificio",
            description = "Recupera un edificio especifico por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    @Operation(
            summary = "Listar unidades de un edificio",
            description = "Retorna las unidades asociadas al edificio indicado."
    )
    @GetMapping("/{id}/units")
    public ResponseEntity<List<Unit>> getBuildingUnits(@PathVariable Long id) {
        log.info("Getting units for building: {}", id);
        List<Unit> units = unitService.getUnitsByBuildingId(id);
        return ResponseEntity.ok(units);
    }

    @Operation(
            summary = "Crear edificio",
            description = "Crea un nuevo edificio con los datos proporcionados."
    )
    @PostMapping
    public ResponseEntity<Building> createBuilding(@RequestBody Building building) {
        return ResponseEntity.ok(buildingService.createBuilding(building));
    }

    @Operation(
            summary = "Actualizar edificio",
            description = "Actualiza atributos de un edificio existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Building> updateBuilding(
            @PathVariable Long id,
            @RequestBody Building buildingDetails) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, buildingDetails));
    }

    @Operation(
            summary = "Eliminar edificio",
            description = "Elimina el edificio indicado por su identificador."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
