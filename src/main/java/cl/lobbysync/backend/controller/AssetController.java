package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.Asset;
import cl.lobbysync.backend.model.mongo.MaintenanceTicket;
import cl.lobbysync.backend.service.AssetService;
import cl.lobbysync.backend.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Slf4j
@Tag(name = "Assets", description = "Gestion de activos y tickets de mantenimiento")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * GET /api/assets
     * Lista todos los activos del edificio
     */
    @Operation(
            summary = "Listar activos",
            description = "Obtiene los activos registrados; permite filtrar por buildingId."
    )
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets(@RequestParam(required = false) Long buildingId) {
        log.info("Getting all assets");
        if (buildingId != null) {
            return ResponseEntity.ok(assetService.getAssetsByBuildingId(buildingId));
        }
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @Operation(
            summary = "Obtener activo",
            description = "Recupera un activo especifico por su ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable String id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @Operation(
            summary = "Crear activo",
            description = "Crea un nuevo activo y lo asocia al edificio indicado."
    )
    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        log.info("Creating asset: {}", asset.getName());
        Asset created = assetService.createAsset(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Actualizar activo",
            description = "Actualiza datos basicos de un activo existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable String id,
            @RequestBody Asset assetDetails) {
        return ResponseEntity.ok(assetService.updateAsset(id, assetDetails));
    }

    /**
     * POST /api/assets/{id}/ticket
     * Abre un nuevo ticket de mantenciИn para un activo
     */
    @Operation(
            summary = "Crear ticket de mantenimiento",
            description = "Abre un ticket asociado al activo y registra quien lo reporta."
    )
    @PostMapping("/{id}/ticket")
    public ResponseEntity<MaintenanceTicket> createMaintenanceTicket(
            @PathVariable String id,
            @RequestBody MaintenanceTicket ticket,
            Authentication authentication) {
        
        log.info("Creating maintenance ticket for asset: {}", id);
        
        ticket.setAssetId(id);
        // reportedBy ya viene en el request body como String
        
        MaintenanceTicket created = maintenanceService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Listar tickets de un activo",
            description = "Obtiene todos los tickets asociados al activo indicado."
    )
    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<MaintenanceTicket>> getAssetTickets(@PathVariable String id) {
        return ResponseEntity.ok(maintenanceService.getTicketsByAssetId(id));
    }

    @Operation(
            summary = "Eliminar activo",
            description = "Elimina un activo por su identificador."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // String firebaseUid = (String) authentication.getPrincipal();
            // TODO: Implementar bカsqueda de userId por firebaseUid
            return 1L;
        }
        return 1L;
    }
}
