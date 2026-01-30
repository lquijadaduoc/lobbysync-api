# PostgreSQL Authentication Fix

## 🚨 PROBLEMA CRÍTICO

**Error encontrado**: `FATAL: password authentication failed for user "postgres"`

**Síntomas**:
- Backend container se inicia pero falla y sale con código 1
- PostgreSQL health check pasa pero conexiones desde backend fallan
- Logs muestran `scram-sha-256` authentication error

## 🔍 CAUSA RAÍZ

PostgreSQL 15 por defecto usa **autenticación `scram-sha-256`** en lugar de `md5`. Cuando existe un volumen persistente con configuración anterior, las credenciales `postgres/postgres` no funcionan correctamente.

## ✅ SOLUCIÓN

### 1. Modificar docker-compose.yml

**AGREGAR** la variable de entorno crítica:

```yaml
postgres_db:
  environment:
    POSTGRES_USER: postgres
    POSTGRES_PASSWORD: postgres
    POSTGRES_DB: lobbysync
    POSTGRES_HOST_AUTH_METHOD: md5  # ← LÍNEA CRÍTICA
```

### 2. Limpiar volúmenes existentes

```bash
# Parar todos los servicios
docker compose down -v

# Eliminar volumen PostgreSQL corrupto
docker volume rm lobbysync-api_postgres_data

# Recrear con configuración correcta
docker compose up -d
```

### 3. Verificar que funciona

```bash
# Probar conexión desde el network de Docker
docker run --rm --network lobbysync-api_lobbysync_network \
  -e PGPASSWORD=postgres postgres:15 \
  psql -h postgres_db -U postgres -d lobbysync -c 'SELECT 1;'
```

## 🔧 AUTOMATIZACIÓN

El script `deploy-vps.sh` ahora incluye automáticamente la limpieza de volúmenes PostgreSQL para prevenir este error.

## ✅ VERIFICACIÓN FINAL

**API debe responder**:
```bash
curl http://localhost:8080/actuator/health
# Resultado esperado: {"status":"UP"}
```

**Creación de usuario debe funcionar**:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@ejemplo.com","password":"123456","name":"Test User","role":"RESIDENT"}'
```

---

**Fecha de resolución**: 30 de enero de 2026  
**Impacto**: CRÍTICO - Sin esta fix, la API no funciona en production  
**Estado**: ✅ RESUELTO - Implementado en ambientes local y VPS