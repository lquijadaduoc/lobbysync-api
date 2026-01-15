package cl.lobbysync.backend.model.sql;

public enum InvitationStatus {
    PENDING,     // ⏳ Invitación creada, esperando llegada del visitante
    ENTERED,     // 🟢 Visitante dentro del edificio (reloj corriendo)
    EXITED,      // 🔵 Visitante salió del edificio
    EXPIRED      // 🚫 Invitación expiró sin usarse
}
