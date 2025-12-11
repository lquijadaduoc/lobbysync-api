package cl.lobbysync.backend.model.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Builder.Default
    private Boolean isPaid = false;
}
