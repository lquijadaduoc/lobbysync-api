package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "access_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {
    @Id
    private String id;

    private Long userId;

    private String accessType; // Legacy field
    
    private String type; // Nuevo: "QR_ENTRY" o "DELIVERY"

    private String location;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String description;

    private Boolean isAuthorized;
    
    // Campos para QR Entry
    private Long invitationId;
    private String guestName;
    private String guestRut;
    private String qrToken;
    
    // Campos para Delivery y unidad
    private Long unitId;
    private String photoUrl;
}
