package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_guests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationGuest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String rut;
    
    @Column
    private String phone;
    
    @Column(name = "checked_in")
    private Boolean checkedIn = false;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
