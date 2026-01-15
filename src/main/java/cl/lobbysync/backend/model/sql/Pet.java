package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false)
    private String name;

    @Column(name = "species")
    private String species; // DOG, CAT, BIRD, OTHER

    @Column(name = "breed")
    private String breed;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size; // SMALL, MEDIUM, LARGE

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "is_dangerous")
    private Boolean isDangerous;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
