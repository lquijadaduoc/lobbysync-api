package cl.lobbysync.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    private String firstName;
    
    private String lastName;
    
    @Email(message = "Email debe ser válido")
    private String email;
    
    private String phone;
    
    private String role; // ADMIN, CONCIERGE, RESIDENT, SUPER_ADMIN
    
    private Long unitId; // Solo para RESIDENT
    
    private Boolean isActive;
}
