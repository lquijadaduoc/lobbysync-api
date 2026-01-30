# Deployment en VPS - LobbySyncAPI

## Información del VPS
- **IP:** 168.197.50.14
- **Usuario:** root
- **Directorio:** /opt/lobbysync-api

## Requisitos previos

### En el VPS
1. Docker y Docker Compose instalados
2. Git instalado
3. `serviceAccountKey.json` en `/opt/lobbysync-api/`

### Variables de entorno
El archivo `docker-compose.yml` contiene todas las variables necesarias.

## Deployment automático

### Opción 1: Despliegue manual en VPS

```bash
# SSH al VPS
ssh root@168.197.50.14

# Clonar/actualizar repositorio
cd /opt/lobbysync-api
git pull origin main

# Levantar servicios
docker-compose down -v
docker-compose up -d

# Verificar que está corriendo
curl http://localhost:8080/actuator/health
```

### Opción 2: Despliegue desde Mac con SSH

```bash
# Ejecutar desde tu Mac
ssh root@168.197.50.14 << 'EOF'
cd /opt/lobbysync-api
git pull origin main
docker-compose down -v
docker image rm lobbysync-api-backend 2>/dev/null
docker-compose up -d
sleep 40
curl -s http://localhost:8080/actuator/health | jq .
EOF
```

## Estructura de servicios

### PostgreSQL
- Puerto: 5432
- Usuario: lobbysync
- Contraseña: (desde env)
- BD: lobbysync_db

### MongoDB
- Puerto: 27017
- BD: lobbysync_logs

### Backend Spring Boot
- Puerto: 8080
- URL: http://168.197.50.14:8080

## Verificación post-despliegue

```bash
# Health check
curl http://168.197.50.14:8080/actuator/health

# Swagger UI
curl http://168.197.50.14:8080/swagger-ui.html

# Crear usuario de prueba
curl -X POST http://168.197.50.14:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@test.com",
    "password": "TestPass123!",
    "firstName": "Test",
    "lastName": "User",
    "role": "RESIDENT"
  }'
```

## Logs

```bash
# Ver logs del backend
docker logs -f lobbysync_backend

# Ver logs de PostgreSQL
docker logs -f postgres_db

# Ver logs de MongoDB
docker logs -f mongo_db
```

## Archivos importantes

- `docker-compose.yml` - Configuración de servicios
- `Dockerfile` - Build de la imagen
- `application.properties` - Configuración Spring Boot
- `serviceAccountKey.json` - Credenciales Firebase (debe ser copiado al VPS)

## Firebase Configuration

Firebase está configurado para usar `serviceAccountKey.json`. Asegúrate de que este archivo está en `/opt/lobbysync-api/` en el VPS.

## Rollback

Si necesitas volver a una versión anterior:

```bash
ssh root@168.197.50.14 << 'EOF'
cd /opt/lobbysync-api
git log --oneline -10
git checkout <commit-hash>
docker-compose down
docker image rm lobbysync-api-backend
docker-compose up -d
EOF
```
