package cl.lobbysync.backend.service;

import cl.lobbysync.backend.model.sql.Invitation;
import cl.lobbysync.backend.model.sql.InvitationStatus;
import cl.lobbysync.backend.repository.InvitationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    public List<Invitation> getAllInvitations() {
        return invitationRepository.findAll();
    }

    public Invitation getInvitationById(Long id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
    }

    public Invitation getInvitationByQrToken(String qrToken) {
        return invitationRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
    }

    public List<Invitation> getInvitationsByUnitId(Long unitId) {
        return invitationRepository.findByUnitId(unitId);
    }

    public List<Invitation> getInvitationsByStatus(InvitationStatus status) {
        return invitationRepository.findByStatus(status);
    }

    public Invitation createInvitation(Invitation invitation) {
        log.info("Creating invitation for guest: {} to unit: {}", 
                invitation.getGuestName(), invitation.getUnitId());
        
        // Establecer valores por defecto si no están presentes
        if (invitation.getStatus() == null) {
            invitation.setStatus(InvitationStatus.ACTIVE);
        }
        
        return invitationRepository.save(invitation);
    }

    public void deleteInvitation(Long id) {
        invitationRepository.deleteById(id);
    }
}
