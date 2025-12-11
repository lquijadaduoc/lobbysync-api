package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.Bill;
import cl.lobbysync.backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FinanceService {

    @Autowired
    private BillRepository billRepository;

    public Bill createBill(Long unitId, Integer month, Integer year, BigDecimal totalAmount) {
        Bill bill = Bill.builder()
                .unitId(unitId)
                .month(month)
                .year(year)
                .totalAmount(totalAmount)
                .isPaid(false)
                .build();
        return billRepository.save(bill);
    }

    public List<Bill> getBillsByUnitAndYear(Long unitId, Integer year) {
        return billRepository.findByUnitIdAndYear(unitId, year);
    }

    public List<Bill> getUnpaidBills() {
        return billRepository.findByIsPaid(false);
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public Bill markBillAsPaid(Long billId) {
        Bill bill = getBillById(billId);
        bill.setIsPaid(true);
        return billRepository.save(bill);
    }

    public void deleteBill(Long billId) {
        billRepository.deleteById(billId);
    }
}
