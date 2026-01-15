package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.mongo.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {
    List<DocumentEntity> findByCategory(String category);
    List<DocumentEntity> findByBuildingId(Long buildingId);
    List<DocumentEntity> findByIsPublic(Boolean isPublic);
}
