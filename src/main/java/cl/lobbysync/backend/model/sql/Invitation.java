package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitations", indexes = {
    @Index(name = "idx_qr_token", columnList = "qrToken", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String guestName;

    @Column(nullable = true)
    private String guestRut;

    @NotNull
    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false, unique = true, length = 36)
    private String qrToken;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.ACTIVE;

    @Column
    private LocalDateTime usedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Genera un token QR único antes de persistir
     */
    @PrePersist
    protected void onCreate() {
        if (this.qrToken == null) {
            this.qrToken = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza el timestamp antes de actualizar
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Método burn: Marca la invitación como USED y guarda la fecha actual
     */
    public void burn() {
        this.status = InvitationStatus.USED;
        this.usedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Valida si el QR es válido
     * @return true si status == ACTIVE y no ha expirado
     */
    public boolean isValid() {
        return this.status == InvitationStatus.ACTIVE 
            && LocalDateTime.now().isBefore(this.validUntil);
    }

    /**
     * Verifica si la invitación ha expirado
     * @return true si la fecha actual es después de validUntil
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.validUntil);
    }

    /**
     * Marca automáticamente como EXPIRED si ha pasado la fecha de validez
     */
    public void checkAndExpire() {
        if (this.status == InvitationStatus.ACTIVE && isExpired()) {
            this.status = InvitationStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
        }
    }
}
