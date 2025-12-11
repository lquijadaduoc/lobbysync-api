package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillGenerationRequest {
    private Integer month;
    private Integer year;
    private BigDecimal totalAmount;
    private String description;
}
