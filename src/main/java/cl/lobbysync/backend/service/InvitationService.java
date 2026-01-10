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
            invitation.setStatus(InvitationStatus.ACTIVE);
        }
        
        return invitationRepository.save(invitation);
    }

    public void deleteInvitation(Long id) {
        invitationRepository.deleteById(id);
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
            
            // Verificar si ya fue usado
            if (invitation.getUsedAt() != null) {
                return QrValidationResponse.builder()
                        .valid(false)
                        .message("Este código QR ya fue utilizado")
                        .visitorName(invitation.getGuestName())
                        .visitorRut(invitation.getGuestRut())
                        .alreadyUsed(true)
                        .usedAt(invitation.getUsedAt())
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
            
            // QR válido - marcar como usado
            invitation.setUsedAt(now);
            invitation.setStatus(InvitationStatus.USED);
            invitationRepository.save(invitation);
            
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
