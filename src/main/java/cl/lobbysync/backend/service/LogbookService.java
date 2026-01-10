package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.LogbookEntry;
import cl.lobbysync.backend.repository.LogbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LogbookService {

    private final LogbookRepository logbookRepository;

    public List<LogbookEntry> getAllEntries() {
        log.info("Getting all logbook entries");
        return logbookRepository.findAllByOrderByTimestampDesc();
    }

    public Page<LogbookEntry> getEntriesPaginated(int page, int size) {
        log.info("Getting paginated logbook entries - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return logbookRepository.findAll(pageable);
    }

    public Optional<LogbookEntry> getEntryById(Long id) {
        log.info("Getting logbook entry by id: {}", id);
        return logbookRepository.findById(id);
    }

    public LogbookEntry createEntry(LogbookEntry entry) {
        log.info("Creating logbook entry - user: {}", entry.getUser());
        if (entry.getTimestamp() == null) {
            entry.setTimestamp(LocalDateTime.now());
        }
        return logbookRepository.save(entry);
    }

    public LogbookEntry updateEntry(Long id, LogbookEntry entryData) {
        log.info("Updating logbook entry id: {}", id);
        LogbookEntry existing = logbookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Logbook entry not found with id: " + id));
        
        if (entryData.getNote() != null) {
            existing.setNote(entryData.getNote());
        }
        if (entryData.getUser() != null) {
            existing.setUser(entryData.getUser());
        }
        if (entryData.getTimestamp() != null) {
            existing.setTimestamp(entryData.getTimestamp());
        }
        
        return logbookRepository.save(existing);
    }

    public void deleteEntry(Long id) {
        log.info("Deleting logbook entry id: {}", id);
        if (!logbookRepository.existsById(id)) {
            throw new RuntimeException("Logbook entry not found with id: " + id);
        }
        logbookRepository.deleteById(id);
    }

    public List<LogbookEntry> getEntriesByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Getting logbook entries from {} to {}", start, end);
        return logbookRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
}
