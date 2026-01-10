package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserData user;
    private String message;
    
    // Mantener campos legacy para compatibilidad
    private String email;
    private String role;
    private Long userId;
}
