package cl.lobbysync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillGenerationRequest {
    private Long buildingId;
    private Integer month;
    private Integer year;
    private BigDecimal baseAmount;
    private LocalDate dueDate;
    private String description;
    
    // Alias para backward compatibility
    public BigDecimal getTotalAmount() {
        return baseAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.baseAmount = totalAmount;
    }
}
