package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.model.mongo.AssetRecord;
import cl.lobbysync.backend.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping
    public ResponseEntity<AssetRecord> createAssetRecord(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        
        AssetRecord record = assetService.createAssetRecord(
            request.get("assetCode"),
            request.get("assetType"),
            request.get("location"),
            request.get("description"),
            request.get("condition"),
            userId
        );
        
        return ResponseEntity.ok(record);
    }

    @GetMapping("/code/{assetCode}")
    public ResponseEntity<List<AssetRecord>> getAssetsByCode(@PathVariable String assetCode) {
        return ResponseEntity.ok(assetService.getAssetsByCode(assetCode));
    }

    @GetMapping("/type/{assetType}")
    public ResponseEntity<List<AssetRecord>> getAssetsByType(@PathVariable String assetType) {
        return ResponseEntity.ok(assetService.getAssetsByType(assetType));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AssetRecord>> getAssetsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(assetService.getAssetsByStatus(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AssetRecord> updateAssetStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(assetService.updateAssetStatus(id, status));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String firebaseUid = (String) authentication.getPrincipal();
            return 1L;
        }
        return 1L;
    }
}
