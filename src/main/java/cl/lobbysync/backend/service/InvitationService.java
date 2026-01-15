package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.QrValidationResponse;
import cl.lobbysync.backend.model.sql.Invitation;
import cl.lobbysync.backend.model.sql.InvitationStatus;
import cl.lobbysync.backend.repository.InvitationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
            invitation.setStatus(InvitationStatus.PENDING);
        }
        
        return invitationRepository.save(invitation);
    }

    public List<Invitation> getInvitationsByCreatedBy(Long userId) {
        return invitationRepository.findByCreatedByOrderByCreatedAtDesc(userId);
    }

    public void deleteInvitation(Long id) {
        invitationRepository.deleteById(id);
    }

    public Invitation updateInvitation(Long id, Invitation invitationDetails) {
        Invitation invitation = getInvitationById(id);
        
        if (invitationDetails.getStatus() != null) {
            invitation.setStatus(invitationDetails.getStatus());
        }
        
        if (invitationDetails.getEntryTime() != null) {
            invitation.setEntryTime(invitationDetails.getEntryTime());
        }
        
        if (invitationDetails.getExitTime() != null) {
            invitation.setExitTime(invitationDetails.getExitTime());
        }
        
        if (invitationDetails.getNotes() != null) {
            invitation.setNotes(invitationDetails.getNotes());
        }
        
        log.info("Updated invitation: {}", id);
        return invitationRepository.save(invitation);
    }

    public Invitation markEntry(Long id) {
        Invitation invitation = getInvitationById(id);
        invitation.setEntryTime(LocalDateTime.now());
        invitation.setStatus(InvitationStatus.ENTERED);
        log.info("Marked entry for invitation: {}", id);
        return invitationRepository.save(invitation);
    }

    public Invitation markExit(Long id) {
        Invitation invitation = getInvitationById(id);
        invitation.setExitTime(LocalDateTime.now());
        invitation.setStatus(InvitationStatus.EXITED);
        log.info("Marked exit for invitation: {}", id);
        return invitationRepository.save(invitation);
    }

    public QrValidationResponse validateQrToken(String qrToken) {
        try {
            Invitation invitation = invitationRepository.findByQrToken(qrToken)
                    .orElse(null);
            
            if (invitation == null) {
                return QrValidationResponse.builder()
                        .valid(false)
                        .message("Código QR no válido o no encontrado")
                        .build();
            }
            
            LocalDateTime now = LocalDateTime.now();
            
            // Verificar si ya ingresó o salió (ciclo completado)
            if (invitation.getStatus() == InvitationStatus.ENTERED || 
                invitation.getStatus() == InvitationStatus.EXITED) {
                return QrValidationResponse.builder()
                        .valid(false)
                        .message("Esta invitación ya fue utilizada")
                        .visitorName(invitation.getGuestName())
                        .visitorRut(invitation.getGuestRut())
                        .alreadyUsed(true)
                        .usedAt(invitation.getEntryTime())
                        .build();
            }
            
            // Verificar si expiró
            if (invitation.getValidUntil() != null && now.isAfter(invitation.getValidUntil())) {
                return QrValidationResponse.builder()
                        .valid(false)
                        .message("Este código QR ha expirado")
                        .visitorName(invitation.getGuestName())
                        .visitorRut(invitation.getGuestRut())
                        .validUntil(invitation.getValidUntil())
                        .build();
            }
            
            // QR válido - NO marcar como usado aún, solo validar
            // El concierge marcará entrada con el botón "Marcar Entrada"
            return QrValidationResponse.builder()
                    .valid(true)
                    .message("Ingreso autorizado")
                    .visitorName(invitation.getGuestName())
                    .visitorRut(invitation.getGuestRut())
                    .validUntil(invitation.getValidUntil())
                    .alreadyUsed(false)
                    .build();
            
        } catch (Exception e) {
            log.error("Error validating QR token", e);
            return QrValidationResponse.builder()
                    .valid(false)
                    .message("Error al validar el código QR")
                    .build();
        }
    }
}
