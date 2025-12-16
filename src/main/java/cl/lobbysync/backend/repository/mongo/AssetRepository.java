package cl.lobbysync.backend.repository.mongo;

import cl.lobbysync.backend.model.mongo.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {
    List<Asset> findByBuildingId(Long buildingId);
    List<Asset> findByType(String type);
    List<Asset> findByStatus(String status);
}
