package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.Unit;
import cl.lobbysync.backend.model.sql.WhitelistContact;
import cl.lobbysync.backend.repository.UnitRepository;
import cl.lobbysync.backend.repository.WhitelistContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class WhitelistService {

    @Autowired
    private WhitelistContactRepository whitelistContactRepository;

    @Autowired
    private UnitRepository unitRepository;

    public List<WhitelistContact> getWhitelistByUnit(Long unitId) {
        return whitelistContactRepository.findByUnitId(unitId);
    }

    public WhitelistContact getWhitelistContactById(Long id) {
        return whitelistContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Whitelist contact not found"));
    }

    @Transactional
    public WhitelistContact createWhitelistContact(Long unitId, WhitelistContact contact) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        
        contact.setUnit(unit);
        return whitelistContactRepository.save(contact);
    }

    @Transactional
    public WhitelistContact updateWhitelistContact(Long id, WhitelistContact updates) {
        WhitelistContact existing = getWhitelistContactById(id);
        
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getRut() != null) existing.setRut(updates.getRut());
        if (updates.getRelationship() != null) existing.setRelationship(updates.getRelationship());
        if (updates.getPhone() != null) existing.setPhone(updates.getPhone());
        if (updates.getHasPermaAccess() != null) existing.setHasPermaAccess(updates.getHasPermaAccess());
        if (updates.getNotes() != null) existing.setNotes(updates.getNotes());
        
        return whitelistContactRepository.save(existing);
    }

    @Transactional
    public void deleteWhitelistContact(Long id) {
        whitelistContactRepository.deleteById(id);
    }
}
