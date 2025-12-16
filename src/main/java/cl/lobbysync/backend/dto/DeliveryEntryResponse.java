package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryEntryResponse {
    private String accessLogId;
    private Long unitId;
    private String photoUrl;
    private LocalDateTime entryTime;
    private String message;
}
