package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "asset_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetRecord {
    @Id
    private String id;

    private String assetCode;

    private String assetType;

    private String location;

    private String status;

    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();

    private Long recordedBy;

    private String description;

    private String condition;
}
