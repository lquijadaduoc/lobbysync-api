package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.mongo.AssetRecord;
import cl.lobbysync.backend.repository.mongo.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public AssetRecord createAssetRecord(String assetCode, String assetType, String location, 
                                        String description, String condition, Long recordedBy) {
        AssetRecord record = AssetRecord.builder()
                .assetCode(assetCode)
                .assetType(assetType)
                .location(location)
                .description(description)
                .condition(condition)
                .status("ACTIVE")
                .recordedBy(recordedBy)
                .recordedAt(LocalDateTime.now())
                .build();
        return assetRepository.save(record);
    }

    public List<AssetRecord> getAssetsByCode(String assetCode) {
        return assetRepository.findByAssetCode(assetCode);
    }

    public List<AssetRecord> getAssetsByType(String assetType) {
        return assetRepository.findByAssetType(assetType);
    }

    public List<AssetRecord> getAssetsByStatus(String status) {
        return assetRepository.findByStatus(status);
    }

    public AssetRecord updateAssetStatus(String id, String status) {
        AssetRecord record = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset record not found"));
        record.setStatus(status);
        return assetRepository.save(record);
    }
}
