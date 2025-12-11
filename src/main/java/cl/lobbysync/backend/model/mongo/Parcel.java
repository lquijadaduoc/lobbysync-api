package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "parcels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {
    @Id
    private String id;

    private Long userId;

    private String trackingNumber;

    private String status;

    @Builder.Default
    private LocalDateTime receivedAt = LocalDateTime.now();

    private LocalDateTime retrievedAt;

    private String location;

    private String description;

    private String carrier;
}
