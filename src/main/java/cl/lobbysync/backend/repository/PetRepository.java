package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUnitId(Long unitId);
    void deleteByUnitId(Long unitId);
}
