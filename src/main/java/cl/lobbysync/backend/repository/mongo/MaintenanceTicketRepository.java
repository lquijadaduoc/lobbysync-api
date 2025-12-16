package cl.lobbysync.backend.repository.mongo;

import cl.lobbysync.backend.model.mongo.MaintenanceTicket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTicketRepository extends MongoRepository<MaintenanceTicket, String> {
    List<MaintenanceTicket> findByAssetId(String assetId);
    List<MaintenanceTicket> findByBuildingId(Long buildingId);
    List<MaintenanceTicket> findByStatus(String status);
    List<MaintenanceTicket> findByAssignedTo(Long assignedTo);
}
