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
public class QrEntryResponse {
    private Long invitationId;
    private String guestName;
    private String guestRut;
    private Long unitId;
    private LocalDateTime entryTime;
    private String message;
}
