package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessEntryRequest {
    private String accessType;
    private String location;
    private String description;
}
