package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "maintenance_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTicket {
    
    @Id
    private String id;

    private String assetId; // Referencia al Asset en MongoDB

    private Long buildingId;

    private String title;

    private String description;

    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED

    private String reportedBy; // Nombre o ID del usuario

    private Long assignedTo; // User ID del técnico

    private LocalDateTime reportedDate;

    private LocalDateTime resolvedDate;

    private String resolution;

    private String photoUrl;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
