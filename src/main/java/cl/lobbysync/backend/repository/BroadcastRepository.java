package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.mongo.Broadcast;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BroadcastRepository extends MongoRepository<Broadcast, String> {
    List<Broadcast> findByTargetAudience(String targetAudience);
    List<Broadcast> findByPriority(String priority);
    List<Broadcast> findBySentAtAfter(LocalDateTime dateTime);
    List<Broadcast> findByIsActive(Boolean isActive);
}
