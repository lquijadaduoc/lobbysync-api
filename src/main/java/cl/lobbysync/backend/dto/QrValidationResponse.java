package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrValidationResponse {
    private Boolean valid;
    private String message;
    private String visitorName;
    private String visitorRut;
    private String unitNumber;
    private String residentName;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean alreadyUsed;
    private LocalDateTime usedAt;
}
