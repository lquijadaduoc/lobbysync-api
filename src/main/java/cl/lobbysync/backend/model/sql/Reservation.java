package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "common_area_id", nullable = false)
    private Long commonAreaId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "unit_id")
    private Long unitId;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "total_amount")
    private Double totalAmount;
    
    @Column(name = "payment_proof_url")
    private String paymentProofUrl;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "requires_cleaning")
    private Boolean requiresCleaning = false;
    
    @Column(name = "cleaning_notes")
    private String cleaningNotes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ReservationStatus {
        PENDING,      // Esperando aprobación
        APPROVED,     // Aprobada por admin
        REJECTED,     // Rechazada
        IN_USE,       // En uso (check-in realizado)
        COMPLETED,    // Completada (check-out realizado)
        CANCELLED     // Cancelada por el usuario
    }
}
