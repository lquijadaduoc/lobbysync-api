package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.WhitelistContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WhitelistContactRepository extends JpaRepository<WhitelistContact, Long> {
    List<WhitelistContact> findByUnitId(Long unitId);
    void deleteByUnitId(Long unitId);
}
