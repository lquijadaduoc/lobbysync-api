package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelRequest {
    private String trackingNumber;
    private String carrier;
    private String location;
    private String description;
}
