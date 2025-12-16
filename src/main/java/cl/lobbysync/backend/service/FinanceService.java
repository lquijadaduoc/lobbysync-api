package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.BillGenerationRequest;
import cl.lobbysync.backend.dto.DebtResponse;
import cl.lobbysync.backend.dto.PaymentRequest;
import cl.lobbysync.backend.model.sql.Bill;
import cl.lobbysync.backend.model.sql.BillStatus;
import cl.lobbysync.backend.model.sql.Payment;
import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.repository.BillRepository;
import cl.lobbysync.backend.repository.PaymentRepository;
import cl.lobbysync.backend.repository.UnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FinanceService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UnitRepository unitRepository;

    /**
     * Genera gastos comunes para todas las unidades activas según alícuota
     */
    @Transactional
    public List<Bill> generateBills(BillGenerationRequest request) {
        log.info("Generating bills for month: {}, year: {}", request.getMonth(), request.getYear());
        
        List<Unit> activeUnits = unitRepository.findByIsActive(true);
        List<Bill> generatedBills = new ArrayList<>();
        
        // Si no viene dueDate, calcular último día del mes
        LocalDate dueDate = request.getDueDate() != null 
            ? request.getDueDate() 
            : LocalDate.of(request.getYear(), request.getMonth(), 1).plusMonths(1).minusDays(1);

        for (Unit unit : activeUnits) {
            // Calcula el monto según la alícuota
            BigDecimal unitAmount = request.getTotalAmount()
                    .multiply(unit.getAliquot())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            Bill bill = Bill.builder()
                    .unitId(unit.getId())
                    .month(request.getMonth())
                    .year(request.getYear())
                    .amount(unitAmount)
                    .dueDate(dueDate)
                    .description(request.getDescription())
                    .status(BillStatus.PENDING)
                    .build();

            generatedBills.add(billRepository.save(bill));
        }

        log.info("Generated {} bills", generatedBills.size());
        return generatedBills;
    }

    /**
     * Obtiene la deuda total de una unidad
     */
    public DebtResponse getUnitDebt(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        List<Bill> unpaidBills = billRepository.findByUnitIdAndStatus(unitId, BillStatus.PENDING);
        unpaidBills.addAll(billRepository.findByUnitIdAndStatus(unitId, BillStatus.PARTIAL));

        BigDecimal totalDebt = unpaidBills.stream()
                .map(Bill::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DebtResponse.builder()
                .unitId(unitId)
                .unitNumber(unit.getUnitNumber())
                .totalDebt(totalDebt)
                .pendingBills(unpaidBills.size())
                .build();
    }

    /**
     * Registra un pago y actualiza el estado de la cuenta
     */
    @Transactional
    public Payment registerPayment(PaymentRequest request) {
        log.info("Registering payment for bill: {}", request.getBillId());

        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        // Crea el registro de pago
        Payment payment = Payment.builder()
                .billId(bill.getId())
                .unitId(bill.getUnitId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(request.getTransactionReference())
                .notes(request.getNotes())
                .build();

        payment = paymentRepository.save(payment);

        // Actualiza el estado de la cuenta
        bill.addPayment(request.getAmount());
        billRepository.save(bill);

        log.info("Payment registered successfully. Remaining amount: {}", bill.getRemainingAmount());
        return payment;
    }

    public List<Bill> getBillsByUnitAndYear(Long unitId, Integer year) {
        return billRepository.findByUnitIdAndYear(unitId, year);
    }

    public List<Bill> getUnpaidBills() {
        List<Bill> bills = new ArrayList<>();
        bills.addAll(billRepository.findByStatus(BillStatus.PENDING));
        bills.addAll(billRepository.findByStatus(BillStatus.PARTIAL));
        return bills;
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public void deleteBill(Long billId) {
        billRepository.deleteById(billId);
    }

    public List<Payment> getPaymentsByUnitId(Long unitId) {
        return paymentRepository.findByUnitId(unitId);
    }
}
