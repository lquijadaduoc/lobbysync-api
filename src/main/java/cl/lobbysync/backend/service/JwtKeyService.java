package cl.lobbysync.backend.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;

@Service
public class JwtKeyService {

    // Clave estática compartida para firmar y verificar tokens JWT
    // En producción, esto debería venir de variables de entorno
    private final Key jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public Key getKey() {
        return jwtKey;
    }
}
