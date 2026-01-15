package cl.lobbysync.backend.service;

import cl.lobbysync.backend.dto.DeliveryEntryResponse;
import cl.lobbysync.backend.dto.QrEntryResponse;
import cl.lobbysync.backend.exception.ResourceNotFoundException;
import cl.lobbysync.backend.model.mongo.AccessLog;
import cl.lobbysync.backend.model.sql.Invitation;
import cl.lobbysync.backend.model.sql.InvitationStatus;
import cl.lobbysync.backend.repository.InvitationRepository;
import cl.lobbysync.backend.repository.mongo.AccessLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AccessControlService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private StorageService storageService;

    /**
     * Flujo 1: Procesa entrada mediante QR
     * 
     * @param qrToken Token del QR escaneado
     * @return Datos de la visita procesada
     * @throws ResourceNotFoundException si el QR no existe
     * @throws IllegalArgumentException si el QR no es válido o ya fue usado
     */
    @Transactional
    public QrEntryResponse processQrEntry(String qrToken) {
        log.info("Processing QR entry with token: {}", qrToken);

        // 1. Busca la Invitation en SQL
        Invitation invitation = invitationRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));

        // 2. Valida que no esté caducada o ya usada
        if (invitation.getStatus() == InvitationStatus.EXITED) {
            throw new IllegalArgumentException("Esta invitación ya fue utilizada el " + invitation.getEntryTime());
        }

        if (invitation.getStatus() == InvitationStatus.EXPIRED) {
            throw new IllegalArgumentException("Esta invitación ha expirado");
        }

        // Verifica si ha expirado por tiempo
        if (!invitation.isValid()) {
            invitation.checkAndExpire();
            invitationRepository.save(invitation);
            throw new IllegalArgumentException("Esta invitación ha expirado. Válida hasta: " + invitation.getValidUntil());
        }

        // 3. Quema el QR (marca como usado)
        invitation.burn();
        invitationRepository.save(invitation);

        // 4. Guarda registro en MongoDB AccessLog
        AccessLog accessLog = AccessLog.builder()
                .type("QR_ENTRY")
                .invitationId(invitation.getId())
                .guestName(invitation.getGuestName())
                .guestRut(invitation.getGuestRut())
                .unitId(invitation.getUnitId())
                .timestamp(LocalDateTime.now())
                .qrToken(qrToken)
                .build();

        accessLogRepository.save(accessLog);

        log.info("QR entry processed successfully for guest: {}, unit: {}", 
                invitation.getGuestName(), invitation.getUnitId());

        // 5. Retorna los datos de la visita
        return QrEntryResponse.builder()
                .invitationId(invitation.getId())
                .guestName(invitation.getGuestName())
                .guestRut(invitation.getGuestRut())
                .unitId(invitation.getUnitId())
                .entryTime(LocalDateTime.now())
                .message("Acceso autorizado. Bienvenido/a " + invitation.getGuestName())
                .build();
    }

    /**
     * Flujo 2: Procesa entrada de delivery con foto
     * 
     * @param unitId ID de la unidad de destino
     * @param photo Foto del paquete/delivery
     * @return Confirmación del registro
     * @throws IllegalArgumentException si no se proporciona foto o unitId
     */
    @Transactional
    public DeliveryEntryResponse processDeliveryEntry(Long unitId, MultipartFile photo) {
        log.info("Processing delivery entry for unit: {}", unitId);

        if (unitId == null) {
            throw new IllegalArgumentException("El ID de la unidad es requerido");
        }

        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("La foto del delivery es requerida");
        }

        // 1. Usa StorageService para guardar la foto en disco
        String photoUrl;
        try {
            photoUrl = storageService.uploadFile(photo.getBytes(), photo.getOriginalFilename());
            log.info("Delivery photo uploaded: {}", photoUrl);
        } catch (IOException e) {
            log.error("Error uploading delivery photo: {}", e.getMessage());
            throw new IllegalArgumentException("Error al subir la foto: " + e.getMessage());
        }

        // 2. Guarda registro en MongoDB AccessLog
        AccessLog accessLog = AccessLog.builder()
                .type("DELIVERY")
                .unitId(unitId)
                .photoUrl(photoUrl)
                .timestamp(LocalDateTime.now())
                .build();

        AccessLog savedLog = accessLogRepository.save(accessLog);

        log.info("Delivery entry registered successfully for unit: {}, photo: {}", unitId, photoUrl);

        // 3. Retorna confirmación
        return DeliveryEntryResponse.builder()
                .accessLogId(savedLog.getId())
                .unitId(unitId)
                .photoUrl(photoUrl)
                .entryTime(LocalDateTime.now())
                .message("Delivery registrado exitosamente")
                .build();
    }
}
