package cl.lobbysync.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleUserInfo {
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String picture;
    private String googleId;
    private boolean emailVerified;
}
