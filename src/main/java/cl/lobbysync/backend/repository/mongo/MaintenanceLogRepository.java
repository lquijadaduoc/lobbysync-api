package cl.lobbysync.backend.repository.mongo;

import cl.lobbysync.backend.model.mongo.MaintenanceLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceLogRepository extends MongoRepository<MaintenanceLog, String> {
    List<MaintenanceLog> findByUserId(Long userId);
    List<MaintenanceLog> findByStatus(String status);
    List<MaintenanceLog> findByTimestampAfter(LocalDateTime timestamp);
}
