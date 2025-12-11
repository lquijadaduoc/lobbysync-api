package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "maintenance_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceLog {
    @Id
    private String id;

    private Long userId;

    private String maintenanceType;

    private String location;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String description;

    private String status;
}
