package cl.lobbysync.backend.repository;

import cl.lobbysync.backend.model.sql.LogbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogbookRepository extends JpaRepository<LogbookEntry, Long> {
    
    List<LogbookEntry> findAllByOrderByTimestampDesc();
    
    List<LogbookEntry> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime start, 
        LocalDateTime end
    );
}
