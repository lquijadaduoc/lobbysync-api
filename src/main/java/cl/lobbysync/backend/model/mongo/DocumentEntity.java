package cl.lobbysync.backend.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {

    @Id
    private String id;

    private String title;
    private String description;
    private String fileUrl;
    private String category;
    private Long buildingId;
    private Long uploadedBy;
    private Boolean isPublic;
    private Integer downloadCount;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
}
