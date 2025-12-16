package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBillId(Long billId);
    List<Payment> findByUnitId(Long unitId);
}
