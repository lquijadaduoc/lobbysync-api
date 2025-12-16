package cl.lobbysync.backend.dto;

import cl.lobbysync.backend.model.sql.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotNull
    private Long billId;
    
    @NotNull
    private BigDecimal amount;
    
    @NotNull
    private PaymentMethod paymentMethod;
    
    private String transactionReference;
    private String notes;
}
