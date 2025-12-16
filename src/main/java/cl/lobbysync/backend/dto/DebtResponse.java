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
public class DebtResponse {
    private Long unitId;
    private String unitNumber;
    private BigDecimal totalDebt;
    private Integer pendingBills;
}
