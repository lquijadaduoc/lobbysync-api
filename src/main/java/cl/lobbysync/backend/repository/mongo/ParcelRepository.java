package cl.lobbysync.backend.repository.mongo;

import cl.lobbysync.backend.model.mongo.Parcel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends MongoRepository<Parcel, String> {
    List<Parcel> findByUserId(Long userId);
    Optional<Parcel> findByTrackingNumber(String trackingNumber);
    List<Parcel> findByStatus(String status);
}
