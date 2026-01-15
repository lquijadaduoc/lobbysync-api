package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "broadcasts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Broadcast {

    @Id
    private String id;

    private String type; // ANNOUNCEMENT, ALERT, NEWS
    private String title;
    private String message;
    private String targetAudience; // ALL, RESIDENTS, CONCIERGES
    private String priority; // LOW, NORMAL, HIGH, URGENT
    private String sentBy;
    private LocalDateTime sentAt;
    private LocalDateTime expiresAt;
    private Integer recipientCount;
    private Integer deliveredCount;
    private Integer readCount;
    private Boolean isActive;
}
