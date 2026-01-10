package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationResponse {
    
    private boolean success;
    
    private String message;
    
    private Long userId;
    
    private String firebaseUid;
    
    private String email;
    
    private String role;
}
