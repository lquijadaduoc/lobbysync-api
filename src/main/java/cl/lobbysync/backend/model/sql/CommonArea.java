package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "common_areas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonArea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(name = "hourly_rate")
    private Double hourlyRate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "building_id")
    private Long buildingId;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval = true;
    
    @Column(name = "time_blocks", columnDefinition = "TEXT")
    private String timeBlocks; // JSON string: [{"name":"Almuerzo","start":"12:00","end":"16:00"},...]
    
    @Column(name = "max_advance_days")
    private Integer maxAdvanceDays = 30; // Máximo días de anticipación para reservar
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
