package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    
    @Id
    private String id;

    private Long buildingId;

    private String name;

    private String type; // ELEVATOR, PUMP, BOILER, GENERATOR, etc.

    private String location;

    private String status; // OPERATIONAL, MAINTENANCE, BROKEN

    private LocalDateTime installationDate;

    private LocalDateTime lastMaintenanceDate;

    private LocalDateTime nextMaintenanceDate;

    private Map<String, Object> specifications; // Datos flexibles (marca, modelo, capacidad, etc.)

    private String notes;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
