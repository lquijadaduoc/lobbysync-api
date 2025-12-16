package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Bill;
import cl.lobbysync.backend.model.sql.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUnitIdAndYear(Long unitId, Integer year);
    List<Bill> findByStatus(BillStatus status);
    List<Bill> findByUnitIdAndStatus(Long unitId, BillStatus status);
    List<Bill> findByUnitId(Long unitId);
}
