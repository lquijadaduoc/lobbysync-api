package cl.lobbysync.backend.model.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "buildings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String address;

    @Column(name = "floors")
    private Integer floors;  // Número de pisos del edificio

    @Transient  // No se persiste en DB, se calcula dinámicamente
    private Integer totalUnits;  // Total de unidades (calculado)

    @Builder.Default
    private Boolean isActive = true;
}
