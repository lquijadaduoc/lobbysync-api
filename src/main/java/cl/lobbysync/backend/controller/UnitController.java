package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@Slf4j
public class UnitController {

    @Autowired
    private UnitService unitService;

    @GetMapping
    public ResponseEntity<List<Unit>> getAllUnits() {
        return ResponseEntity.ok(unitService.getAllUnits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getUnitById(id));
    }

    @PostMapping
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) {
        return ResponseEntity.ok(unitService.createUnit(unit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Unit> updateUnit(
            @PathVariable Long id,
            @RequestBody Unit unitDetails) {
        return ResponseEntity.ok(unitService.updateUnit(id, unitDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
