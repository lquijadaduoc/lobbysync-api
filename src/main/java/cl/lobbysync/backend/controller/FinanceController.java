package cl.lobbysync.backend.controller;

import cl.lobbysync.backend.dto.BillGenerationRequest;
import cl.lobbysync.backend.model.sql.Bill;
import cl.lobbysync.backend.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @PostMapping
    public ResponseEntity<Bill> createBill(@RequestBody BillGenerationRequest request) {
        Bill bill = financeService.createBill(
            1L,
            request.getMonth(),
            request.getYear(),
            request.getTotalAmount()
        );
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/unit/{unitId}/year/{year}")
    public ResponseEntity<List<Bill>> getBillsByUnitAndYear(
            @PathVariable Long unitId,
            @PathVariable Integer year) {
        return ResponseEntity.ok(financeService.getBillsByUnitAndYear(unitId, year));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Bill>> getUnpaidBills() {
        return ResponseEntity.ok(financeService.getUnpaidBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(financeService.getBillById(id));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Bill> markBillAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(financeService.markBillAsPaid(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        financeService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }
}
