# 🚀 LobbySync API - Production Deployment

## Información del Servidor de Producción

**Fecha de Deployment**: 15 de Enero de 2026  
**Estado**: ✅ **ACTIVO Y FUNCIONAL**

### Servidor VPS
- **IP/Host**: `168.197.50.14`
- **Usuario**: `root`
- **Plataforma**: Linux (Docker)
- **Contratista**: VPS Provider

---

## 🌐 Acceso a la API

### URLs Principales
| Servicio | URL | Estado |
|----------|-----|--------|
| **API Base** | http://168.197.50.14:8080 | ✅ 200 OK |
| **Swagger UI** | http://168.197.50.14:8080/swagger-ui.html | ✅ Disponible |
| **API Docs (OpenAPI)** | http://168.197.50.14:8080/v3/api-docs | ✅ Disponible |
| **PostgreSQL** | 168.197.50.14:5432 | ✅ Conectado |
| **MongoDB** | 168.197.50.14:27017 | ✅ Conectado |

### Endpoints Verificados
```bash
# Test GET - Usuarios
curl -X GET http://168.197.50.14:8080/api/v1/users

# Test GET - Edificios  
curl -X GET http://168.197.50.14:8080/api/v1/buildings

# Test GET - Unidades
curl -X GET http://168.197.50.14:8080/api/v1/units

# Test Health Check
curl -X GET http://168.197.50.14:8080/actuator/health
```

---

## 🐳 Contenedores Docker

### Estado Actual
```
lobbysync_backend    Up 2+ hours    0.0.0.0:8080->8080/tcp
postgres_db          Up 2+ hours    0.0.0.0:5432->5432/tcp
mongo_db             Up 2+ hours    0.0.0.0:27017->27017/tcp
```

### Gestión de Contenedores

#### Ver estado
```bash
ssh root@168.197.50.14
cd /root/lobbysync-api
docker-compose ps
```

#### Ver logs
```bash
docker logs -f lobbysync_backend       # Logs del API
docker logs -f postgres_db             # Logs de PostgreSQL
docker logs -f mongo_db                # Logs de MongoDB
```

#### Reiniciar servicios
```bash
docker-compose restart                 # Reiniciar todos
docker-compose restart lobbysync_backend # Solo el API
```

#### Detener/Iniciar
```bash
docker-compose stop                    # Detener
docker-compose down                    # Detener y remover contenedores
docker-compose up -d                   # Iniciar
```

---

## 📦 Versión Desplegada

### Información del Commit
- **Hash**: `a4a67d4`
- **Rama**: `main`
- **Mensaje**: "feat: Stable backend version with Docker deployment and complete API endpoints"
- **Cambios**: 52 archivos, 2923+ líneas de código

### Componentes Incluidos
- ✅ 4 Controllers nuevos (Broadcast, Document, Home, Whitelist)
- ✅ 9 Entity Models (Broadcast, Document, Pet, Vehicle, Family, etc.)
- ✅ 7 Repositories (MongoDB y PostgreSQL)
- ✅ 4 Services nuevos
- ✅ 82+ endpoints disponibles

---

## 🗄️ Bases de Datos

### PostgreSQL
- **Host**: postgres_db
- **Puerto**: 5432
- **Base de datos**: `lobbysync`
- **Usuario**: `postgres`
- **Password**: `postgres` (ver docker-compose.yml)

### Acceso
```bash
docker exec -it postgres_db psql -U postgres -d lobbysync
```

### Tablas Disponibles
- users
- buildings
- units
- common_areas
- reservations
- pets
- vehicles
- invitations
- family_members
- logbook_entries
- payments
- bills
- whitelists
- Y más...

### MongoDB
- **Host**: mongo_db
- **Puerto**: 27017
- **Acceso**: Sin autenticación (configurado para desarrollo)

### Colecciones Disponibles
- broadcasts
- documents
- Y más...

#### Acceso
```bash
docker exec -it mongo_db mongosh
```

---

## 🔐 Seguridad

### Configuración de Seguridad Actual
- Spring Security: ✅ Activo
- Firebase Auth: ⚠️ Opcional (sin credenciales en producción)
- CORS: ✅ Configurado para desarrollo
- JWT: ✅ Disponible como fallback

### Recomendaciones de Producción
1. **Cambiar contraseñas de base de datos** en `docker-compose.yml`
2. **Configurar HTTPS/SSL** con Let's Encrypt
3. **Agregar Firebase credentials** si se necesita autenticación
4. **Configurar backup automático** de bases de datos
5. **Implementar rate limiting** en endpoints
6. **Configurar logs centralizados** (ELK Stack, etc.)
7. **Monitoreo** con Prometheus/Grafana

---

## 📊 Monitoreo y Logs

### Ver logs en tiempo real
```bash
ssh root@168.197.50.14
cd /root/lobbysync-api
docker-compose logs -f --tail=50
```

### Logs específicos del API
```bash
docker logs -f --tail=100 lobbysync_backend
```

### Búsqueda en logs
```bash
docker logs lobbysync_backend 2>&1 | grep "error"
docker logs lobbysync_backend 2>&1 | grep "Tomcat started"
```

---

## 🔄 Actualizar el Código

### Descargar última versión
```bash
cd /root/lobbysync-api
git fetch origin main
git reset --hard origin/main
git clean -fd
```

### Reconstruir y reiniciar
```bash
docker-compose down -v
docker-compose up -d --build
```

---

## 🚨 Troubleshooting

### API no responde
```bash
# 1. Verificar que contenedores estén ejecutándose
docker ps

# 2. Ver logs del API
docker logs lobbysync_backend

# 3. Verificar puerto
netstat -tulpn | grep 8080

# 4. Reiniciar
docker-compose restart lobbysync_backend
```

### Base de datos sin conexión
```bash
# PostgreSQL
docker logs postgres_db
docker exec -it postgres_db psql -U postgres -c "SELECT 1"

# MongoDB
docker logs mongo_db
docker exec -it mongo_db mongosh --eval "db.version()"
```

### Espacio en disco bajo
```bash
# Limpiar imágenes no usadas
docker image prune -a

# Limpiar volúmenes no usados
docker volume prune

# Ver uso de espacio
docker system df
```

---

## 📞 Acceso SSH

### Conectarse al VPS
```bash
ssh root@168.197.50.14
Password: SebaErica12.18
```

### Ubicaciones importantes
- Repositorio: `/root/lobbysync-api`
- Logs de Docker: Ver con `docker logs`
- Volúmenes: `/var/lib/docker/volumes/`

---

## 🎯 Frontend Integration

### Configurar Frontend para conectar a este API

En el frontend (React), configurar en `axiosConfig.js`:
```javascript
const API_BASE_URL = "http://168.197.50.14:8080";
```

O usar variable de entorno:
```bash
VITE_API_URL=http://168.197.50.14:8080
```

### CORS Configuration
El API está configurado para aceptar requests desde cualquier origen en desarrollo.

---

## 📋 Checklist de Producción

### Completados ✅
- [x] API desplegada en VPS
- [x] Docker configurado y ejecutándose
- [x] PostgreSQL conectado
- [x] MongoDB conectado
- [x] Endpoints respondiendo (HTTP 200)
- [x] Swagger UI disponible
- [x] Git repositorio actualizado

### Pendientes de Hacer
- [ ] Cambiar credenciales de bases de datos
- [ ] Configurar HTTPS
- [ ] Agregar Firebase credentials
- [ ] Configurar backups automáticos
- [ ] Implementar monitoreo
- [ ] Pruebas de carga
- [ ] Documentación de API finalizada
- [ ] Training de equipo

---

## 📞 Contacto

**Responsable del Deployment**: Sebastian  
**Fecha**: 15 de Enero de 2026  
**Versión API**: 1.0.0  
**Estado**: Producción

---

**Última actualización**: 15 de Enero de 2026  
**Status**: ✅ **OPERACIONAL**
