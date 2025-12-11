package cl.lobbysync.backend.repository.mongo;

import cl.lobbysync.backend.model.mongo.AssetRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends MongoRepository<AssetRecord, String> {
    List<AssetRecord> findByAssetCode(String assetCode);
    List<AssetRecord> findByAssetType(String assetType);
    List<AssetRecord> findByStatus(String status);
    Optional<AssetRecord> findByAssetCodeAndLocation(String assetCode, String location);
}
