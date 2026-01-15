package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "whitelist_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhitelistContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false)
    private String name;

    @Column(name = "rut", nullable = false)
    private String rut;

    @Column(name = "relationship")
    private String relationship;

    @Column(name = "phone")
    private String phone;

    @Column(name = "has_perma_access")
    private Boolean hasPermaAccess;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (hasPermaAccess == null) {
            hasPermaAccess = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
