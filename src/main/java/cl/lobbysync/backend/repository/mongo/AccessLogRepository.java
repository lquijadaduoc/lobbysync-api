package cl.lobbysync.backend.repository.mongo;

import cl.lobbysync.backend.model.mongo.AccessLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends MongoRepository<AccessLog, String> {
    List<AccessLog> findByUserId(Long userId);
    List<AccessLog> findByUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);
    List<AccessLog> findByAccessType(String accessType);
}
