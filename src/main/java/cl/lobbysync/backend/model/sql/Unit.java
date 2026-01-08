package cl.lobbysync.backend.model.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String unitNumber;

    @NotNull
    @Column(nullable = false)
    private Long buildingId;

    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquot; // Alícuota (porcentaje de gastos comunes)

    @Column
    private Long ownerId; // Propietario (referencia a User)

    @Column
    private String ownerName; // Nombre del morador/propietario

    @Column
    private String ownerPhone; // Teléfono del morador/propietario

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private String floor;

    @Column
    private Integer surfaceArea; // Metros cuadrados
}
