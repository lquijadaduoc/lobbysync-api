package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.Asset;
import cl.lobbysync.backend.repository.mongo.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public List<Asset> getAssetsByBuildingId(Long buildingId) {
        return assetRepository.findByBuildingId(buildingId);
    }

    public Asset getAssetById(String id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    public Asset createAsset(Asset asset) {
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    public Asset updateAsset(String id, Asset assetDetails) {
        Asset asset = getAssetById(id);
        asset.setName(assetDetails.getName());
        asset.setType(assetDetails.getType());
        asset.setLocation(assetDetails.getLocation());
        asset.setStatus(assetDetails.getStatus());
        asset.setSpecifications(assetDetails.getSpecifications());
        asset.setNotes(assetDetails.getNotes());
        asset.setLastMaintenanceDate(assetDetails.getLastMaintenanceDate());
        asset.setNextMaintenanceDate(assetDetails.getNextMaintenanceDate());
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    public void deleteAsset(String id) {
        assetRepository.deleteById(id);
    }

    public List<Asset> getAssetsByType(String type) {
        return assetRepository.findByType(type);
    }

    public List<Asset> getAssetsByStatus(String status) {
        return assetRepository.findByStatus(status);
    }
}
