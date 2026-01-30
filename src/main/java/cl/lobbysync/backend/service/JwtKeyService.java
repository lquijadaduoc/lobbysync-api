package cl.lobbysync.backend.service;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtKeyService {

    // Clave estática compartida para firmar y verificar tokens JWT
    // En producción, esto debería venir de variables de entorno
    private final SecretKey jwtKey = Jwts.SIG.HS256.key().build();

    public SecretKey getKey() {
        return jwtKey;
    }
}
