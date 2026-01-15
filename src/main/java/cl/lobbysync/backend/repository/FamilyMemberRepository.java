package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByUnitId(Long unitId);
    void deleteByUnitId(Long unitId);
}
