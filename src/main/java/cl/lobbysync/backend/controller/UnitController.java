package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@Slf4j
@Tag(name = "Units", description = "Gestion de unidades dentro de edificios")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Operation(
            summary = "Listar unidades",
            description = "Retorna todas las unidades registradas."
    )
    @GetMapping
    public ResponseEntity<List<Unit>> getAllUnits() {
        return ResponseEntity.ok(unitService.getAllUnits());
    }

    @Operation(
            summary = "Obtener unidad",
            description = "Recupera una unidad especifica por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getUnitById(id));
    }

    @Operation(
            summary = "Crear unidad",
            description = "Crea una nueva unidad vinculada a un edificio."
    )
    @PostMapping
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) {
        return ResponseEntity.ok(unitService.createUnit(unit));
    }

    @Operation(
            summary = "Actualizar unidad",
            description = "Actualiza los datos de una unidad existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Unit> updateUnit(
            @PathVariable Long id,
            @RequestBody Unit unitDetails) {
        return ResponseEntity.ok(unitService.updateUnit(id, unitDetails));
    }

    @Operation(
            summary = "Eliminar unidad",
            description = "Elimina una unidad por su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
