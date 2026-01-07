package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.BillGenerationRequest;
import cl.lobbysync.backend.dto.DebtResponse;
import cl.lobbysync.backend.dto.PaymentRequest;
import cl.lobbysync.backend.model.sql.Bill;
import cl.lobbysync.backend.model.sql.Payment;
import cl.lobbysync.backend.service.FinanceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@Slf4j
@Tag(name = "Finance", description = "Generacion de gastos comunes y pagos")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    /**
     * POST /api/finance/generate
     * Genera gastos comunes del mes para todas las unidades
     */
    @Operation(
            summary = "Generar facturacion mensual",
            description = "Genera gastos comunes para todas las unidades segun mes y anio enviados."
    )
    @PostMapping("/generate")
    public ResponseEntity<List<Bill>> generateBills(@Valid @RequestBody BillGenerationRequest request) {
        log.info("Generating bills for month: {}, year: {}", request.getMonth(), request.getYear());
        List<Bill> bills = financeService.generateBills(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bills);
    }

    /**
     * GET /api/finance/units/{id}/debt
     * Obtiene la deuda total de un departamento
     */
    @Operation(
            summary = "Obtener deuda de unidad",
            description = "Calcula y retorna la deuda total de una unidad especifica."
    )
    @GetMapping("/units/{id}/debt")
    public ResponseEntity<DebtResponse> getUnitDebt(@PathVariable Long id) {
        log.info("Getting debt for unit: {}", id);
        DebtResponse debt = financeService.getUnitDebt(id);
        return ResponseEntity.ok(debt);
    }

    /**
     * POST /api/finance/payments
     * Registra un pago y reduce la deuda
     */
    @Operation(
            summary = "Registrar pago",
            description = "Registra un pago para una boleta y actualiza su estado."
    )
    @PostMapping("/payments")
    public ResponseEntity<Payment> registerPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Registering payment for bill: {}", request.getBillId());
        Payment payment = financeService.registerPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @Operation(
            summary = "Listar boletas por unidad y anio",
            description = "Recupera las boletas de una unidad para el anio indicado."
    )
    @GetMapping("/bills/unit/{unitId}/year/{year}")
    public ResponseEntity<List<Bill>> getBillsByUnitAndYear(
            @PathVariable Long unitId,
            @PathVariable Integer year) {
        return ResponseEntity.ok(financeService.getBillsByUnitAndYear(unitId, year));
    }

    @Operation(
            summary = "Listar boletas impagas",
            description = "Retorna las boletas que mantienen saldo pendiente."
    )
    @GetMapping("/bills/unpaid")
    public ResponseEntity<List<Bill>> getUnpaidBills() {
        return ResponseEntity.ok(financeService.getUnpaidBills());
    }

    @Operation(
            summary = "Obtener boleta",
            description = "Retorna una boleta especifica por ID."
    )
    @GetMapping("/bills/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(financeService.getBillById(id));
    }

    @Operation(
            summary = "Listar pagos por unidad",
            description = "Obtiene el historial de pagos de una unidad."
    )
    @GetMapping("/payments/unit/{unitId}")
    public ResponseEntity<List<Payment>> getPaymentsByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(financeService.getPaymentsByUnitId(unitId));
    }

    @Operation(
            summary = "Eliminar boleta",
            description = "Elimina una boleta por su identificador."
    )
    @DeleteMapping("/bills/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        financeService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }
}
