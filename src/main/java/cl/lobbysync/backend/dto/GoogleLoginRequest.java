package cl.lobbysync.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class GoogleLoginRequest {
    
    @NotBlank(message = "El ID Token de Google es requerido")
    private String idToken;
}
