package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.Asset;
import cl.lobbysync.backend.model.mongo.MaintenanceTicket;
import cl.lobbysync.backend.service.AssetService;
import cl.lobbysync.backend.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Slf4j
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * GET /api/assets
     * Lista todos los activos del edificio
     */
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets(@RequestParam(required = false) Long buildingId) {
        log.info("Getting all assets");
        if (buildingId != null) {
            return ResponseEntity.ok(assetService.getAssetsByBuildingId(buildingId));
        }
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable String id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        log.info("Creating asset: {}", asset.getName());
        Asset created = assetService.createAsset(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable String id,
            @RequestBody Asset assetDetails) {
        return ResponseEntity.ok(assetService.updateAsset(id, assetDetails));
    }

    /**
     * POST /api/assets/{id}/ticket
     * Abre un nuevo ticket de mantención para un activo
     */
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

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<MaintenanceTicket>> getAssetTickets(@PathVariable String id) {
        return ResponseEntity.ok(maintenanceService.getTicketsByAssetId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // String firebaseUid = (String) authentication.getPrincipal();
            // TODO: Implementar búsqueda de userId por firebaseUid
            return 1L;
        }
        return 1L;
    }
}
