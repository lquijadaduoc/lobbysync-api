package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private Long id;
    private String email;
    private String role;
    private String firebaseUid;
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean isActive;
    private Long unitId;
}
