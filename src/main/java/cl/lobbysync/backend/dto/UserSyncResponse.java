package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSyncResponse {
    private Long id;
    private String email;
    private String firebaseUid;
    private String role;
    private Boolean isActive;
    private String message;
    private Boolean isNew;
}
