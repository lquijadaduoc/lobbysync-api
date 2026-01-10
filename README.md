# LobbySync Backend API

Plataforma SaaS para gestión integral de edificios residenciales y comerciales, con integración Firebase para autenticación.

## 🚀 Características

- **Gestión de Edificios**: CRUD completo de propiedades residenciales y comerciales
- **Control de Acceso**: Registro de entrada/salida con timestamps
- **Gestión de Parcelas**: Seguimiento de entregas y paquetería
- **Administración Financiera**: Generación y seguimiento de facturas
- **Gestión de Activos**: Registro de activos y tickets de mantenimiento
- **Autenticación Firebase**: Integración completa con Firebase Authentication
- **Base de datos híbrida**: PostgreSQL (datos transaccionales) + MongoDB (logs y eventos)
- **Documentación Swagger**: OpenAPI 3.0 con UI interactiva
- **Docker**: Despliegue containerizado con compose

## 🏗️ Arquitectura

```
┌─────────────────────┐
│   Firebase Auth     │
│  (Client-Side)      │
└──────────┬──────────┘
           │ ID Token
           ▼
┌──────────────────────────────────┐
│  Spring Boot 4.0.0 Backend       │
│  (Java 17)                       │
├──────────────────────────────────┤
│  ✓ REST API (/api/v1, /api/auth) │
│  ✓ Firebase Token Validation     │
│  ✓ User Sync                     │
│  ✓ Role-Based Access Control     │
│  ✓ Swagger/OpenAPI Docs          │
└──────────┬───────────────────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
┌─────────┐  ┌──────────┐
│PostgreSQL│  │MongoDB   │
│  Users  │  │  Logs    │
│ Buildings│  │ Events   │
│ Finances│  │ Parcels  │
└─────────┘  └──────────┘
```

## 📋 Requisitos Previos

- Docker & Docker Compose
- Java 17+ (para desarrollo local)
- Maven 3.9+
- Firebase Project configurado

## 🔧 Instalación & Despliegue

### Con Docker (Recomendado)

```bash
# Clonar repositorio
git clone https://github.com/lquijadaduoc/lobbysync-api.git
cd lobbysync-api

# Iniciar servicios
docker-compose up -d

# Verificar estado
docker ps
```

### Desarrollo Local

```bash
# Instalar dependencias
mvn clean install

# Configurar variables de entorno
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/edificios_db
export SPRING_DATASOURCE_USERNAME=admin_postgres
export SPRING_DATASOURCE_PASSWORD=postgres_db
export SPRING_MONGODB_HOST=localhost
export SPRING_MONGODB_PORT=27017

# Ejecutar aplicación
mvn spring-boot:run
```

## 🔐 Configuración Firebase

### Backend (Spring Boot)

1. **Crear proyecto en Firebase Console**: https://console.firebase.google.com
2. **Habilitar Authentication** → Email/Password
3. **Descargar serviceAccountKey.json** desde Project Settings → Service Accounts
4. **Guardar en**: `/root/lobbysync-api/serviceAccountKey.json` (servidor)
   - NO commitar a Git (protegido por .gitignore)

### Frontend

El frontend debe configurar Firebase Client SDK:

```javascript
// firebaseConfig.js
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "AIzaSy...",
  authDomain: "lobbysync-91db0.firebaseapp.com",
  projectId: "lobbysync-91db0",
  // ... demás configuración
};

export const auth = getAuth(initializeApp(firebaseConfig));
```

### Usuarios de Prueba

Crear estos usuarios en Firebase Console → Authentication → Users:

| Email | Contraseña | Rol |
|-------|------------|-----|
| superadmin@lobbysync.com | admin123 | SUPER_ADMIN |
| admin@lobbysync.com | admin123 | ADMIN |
| concierge@lobbysync.com | admin123 | CONCIERGE |
| resident@lobbysync.com | admin123 | RESIDENT |

## 📚 API Endpoints

### Autenticación

#### Sincronizar Usuario con Backend
`POST /api/auth/sync-user`

Sincroniza automáticamente el usuario autenticado en Firebase con PostgreSQL.

**Headers**:
```
Authorization: Bearer <firebase-id-token>
```

**Response**:
```json
{
  "id": 1,
  "email": "user@example.com",
  "role": "ADMIN",
  "firebaseUid": "abc123...",
  "isActive": true,
  "isNew": false,
  "message": "User already synchronized"
}
```

#### Verificar Token
`GET /api/auth/verify`

Verifica que el token de Firebase sea válido.

**Headers**:
```
Authorization: Bearer <firebase-id-token>
```

### Usuarios

#### Listar Todos los Usuarios
`GET /api/v1/users`

**Response**:
```json
[
  {
    "id": 1,
    "email": "admin@lobbysync.com",
    "role": "ADMIN",
    "firstName": "Admin",
    "lastName": "User",
    "phone": "+56912345678",
    "firebaseUid": "xyz789...",
    "isActive": true,
    "createdAt": "2026-01-10T12:00:00"
  }
]
```

#### Crear Usuario en Firebase y PostgreSQL
`POST /api/v1/users`

Crea un usuario simultáneamente en Firebase Authentication y PostgreSQL.

**Request**:
```json
{
  "email": "nuevo@lobbysync.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "RESIDENT",
  "phone": "+56987654321"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Usuario creado exitosamente en Firebase y PostgreSQL",
  "userId": 5,
  "firebaseUid": "def456...",
  "email": "nuevo@lobbysync.com",
  "role": "RESIDENT"
}
```

**Roles disponibles**: `SUPER_ADMIN`, `ADMIN`, `CONCIERGE`, `RESIDENT`

#### Obtener Usuario Actual
`GET /api/v1/users/me`

**Headers**:
```
Authorization: Bearer <firebase-id-token>
```

**Response**:
```json
{
  "id": 1,
  "email": "user@example.com",
  "role": "ADMIN",
  "firstName": "Juan",
  "lastName": "Admin",
  "isActive": true
}
```

#### Obtener Usuario por ID
`GET /api/v1/users/{id}`

#### Obtener Usuario por Email
`GET /api/v1/users/email/{email}`

### Bitácora (Logbook)

#### Listar Entradas de Bitácora
`GET /api/v1/logbook`

**Query params**:
- `page` (default: 0)
- `size` (default: 50)

**Response**:
```json
[
  {
    "id": 1,
    "note": "Visita de técnico de mantención",
    "userEmail": "concierge@lobbysync.com",
    "timestamp": "2026-01-10T14:30:00",
    "createdAt": "2026-01-10T14:30:05"
  }
]
```

#### Crear Entrada en Bitácora
`POST /api/v1/logbook`

**Request**:
```json
{
  "note": "Reparación de ascensor completada",
  "user": "concierge@lobbysync.com",
  "timestamp": "2026-01-10T15:00:00"
}
```

#### Actualizar Entrada
`PUT /api/v1/logbook/{id}`

#### Eliminar Entrada
`DELETE /api/v1/logbook/{id}`

#### Buscar por Fecha
`GET /api/v1/logbook/date/{date}`

Formato de fecha: `yyyy-MM-dd` (ej: `2026-01-10`)

### Edificios

#### Listar Edificios
`GET /api/v1/buildings`

**Query params**:
- `page` (default: 0)
- `size` (default: 20)

#### Crear Edificio
`POST /api/v1/buildings`

**Request**:
```json
{
  "name": "Torre Central",
  "address": "Av. Principal 123",
  "city": "Santiago",
  "totalUnits": 50
}
```

#### Obtener Detalles
`GET /api/v1/buildings/{id}`

#### Actualizar Edificio
`PUT /api/v1/buildings/{id}`

#### Eliminar Edificio
`DELETE /api/v1/buildings/{id}`

### Unidades/Departamentos

#### Listar Unidades
`GET /api/v1/units`

**Query params**:
- `buildingId` (opcional)
- `page`, `size`

#### Crear Unidad
`POST /api/v1/units`

**Request**:
```json
{
  "buildingId": 1,
  "number": "101",
  "floor": 1,
  "area": 75.5,
  "occupied": true,
  "residentName": "Juan Pérez"
}
```

#### Actualizar Unidad
`PUT /api/v1/units/{id}`

#### Eliminar Unidad
`DELETE /api/v1/units/{id}`

#### Listar Unidades por Edificio
`GET /api/v1/buildings/{buildingId}/units`

### Control de Acceso
- `GET /api/v1/access/logs` - Listar registros de acceso
- `POST /api/v1/access/entry` - Registrar entrada/salida

### Parcelas
- `GET /api/v1/parcels` - Listar parcelas
- `POST /api/v1/parcels` - Registrar parcela
- `PUT /api/v1/parcels/{id}` - Marcar como entregada

### Finanzas
- `GET /api/v1/bills` - Listar facturas
- `POST /api/v1/finance/bills/generate` - Generar facturas

### Activos
- `POST /api/v1/assets/record` - Registrar activo
- `POST /api/v1/assets/ticket` - Crear ticket de mantenimiento

## 📖 Documentación Swagger

Accesible en: `http://localhost:8080/swagger-ui/index.html`

Visualiza y prueba todos los endpoints interactivamente.

## 🗄️ Base de Datos

### PostgreSQL 15
- **Host**: postgres_db:5432 (Docker) / localhost:5432 (desarrollo)
- **Usuario**: postgres
- **Contraseña**: postgres
- **Base de datos**: lobbysync

**Tablas principales**:
- `users` - Usuarios sincronizados desde Firebase
  - Columnas: id, email, firebase_uid, role, first_name, last_name, phone, is_active, created_at
- `buildings` - Edificios/propiedades
- `units` - Departamentos/unidades
- `bills` - Facturas
- `logbook_entries` - Entradas de bitácora del conserje

### MongoDB
- **Host**: mongo_db:27017 (Docker) / localhost:27017 (desarrollo)
- **Base de datos**: lobbysync

**Colecciones principales**:
- `access_logs` - Registros de entrada/salida
- `parcels` - Entregas y paquetería
- `asset_records` - Registro de activos del edificio
- `maintenance_logs` - Tickets de mantenimiento

## 🛠️ Desarrollo

### Estructura del Proyecto

```
src/
├── main/java/cl/lobbysync/backend/
│   ├── controller/       # REST Controllers
│   ├── service/          # Business Logic
│   ├── repository/       # Data Access
│   ├── model/           # Entities
│   │   ├── sql/         # JPA Entities (PostgreSQL)
│   │   └── mongo/       # MongoDB Documents
│   ├── config/          # Spring Configuration
│   ├── filter/          # Security Filters
│   ├── dto/             # Data Transfer Objects
│   ├── exception/       # Exception Handlers
│   └── BackendApplication.java
└── test/java/           # Unit Tests
```

### Variables de Entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://postgres_db:5432/lobbysync | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | postgres | Usuario PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | postgres | Contraseña PostgreSQL |
| `SPRING_MONGODB_HOST` | mongo_db | Host MongoDB |
| `SPRING_MONGODB_PORT` | 27017 | Puerto MongoDB |
| `SPRING_MONGODB_DATABASE` | lobbysync | Base de datos MongoDB |

**Firebase**:
- El archivo `serviceAccountKey.json` debe estar en la raíz del proyecto
- Se monta automáticamente en el contenedor Docker

## 🧪 Testing

### Probar Autenticación

```bash
# 1. Obtener token de Firebase (desde el frontend)
# El token se obtiene después de login exitoso

# 2. Sincronizar usuario con backend
curl -X POST http://168.197.50.14:8080/api/auth/sync-user \
  -H "Authorization: Bearer <firebase-token>"

# 3. Verificar usuario actual
curl http://168.197.50.14:8080/api/v1/users/me \
  -H "Authorization: Bearer <firebase-token>"
```

### Probar Creación de Usuario

```bash
curl -X POST http://168.197.50.14:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nuevo@test.com",
    "password": "test123456",
    "firstName": "Test",
    "lastName": "User",
    "role": "RESIDENT",
    "phone": "+56912345678"
  }'
```

### Ejecutar Tests Unitarios

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=UserServiceTest

# Generar reporte de coverage
mvn test jacoco:report
```

## 🐛 Troubleshooting

### Puerto ya en uso
```bash
# Encontrar proceso en puerto 8080
lsof -i :8080

# Matar proceso
kill -9 <PID>
```

### Problemas de conexión BD
```bash
# Verificar Docker containers
docker ps

# Ver logs
docker logs lobbysync_backend
docker logs postgres_db
docker logs mongo_db
```

### Firebase token inválido
```bash
# Verificar que serviceAccountKey.json existe
ls -la /Users/tu-usuario/Downloads/serviceAccountKey.json

# Verificar configuración en docker-compose.yml
docker exec lobbysync_backend env | grep FIREBASE
```

## 📝 Cambios Recientes

### v2.0.0 - Firebase Authentication Integration (2026-01-10)
- ✅ **Firebase Admin SDK** integrado para autenticación
- ✅ **Endpoint POST /api/v1/users** - Crear usuarios en Firebase + PostgreSQL
- ✅ **Endpoint POST /api/auth/sync-user** - Sincronización automática
- ✅ **FirebaseTokenFilter** - Validación de tokens en cada request
- ✅ **Tabla users actualizada** - Columnas: first_name, last_name, phone
- ✅ **Bitácora (Logbook)** - Sistema completo CRUD para conserjes
- ✅ **Gestión de Edificios y Unidades** - CRUD completo
- ✅ **Despliegue en VPS** - 168.197.50.14:8080
- ✅ **Docker Compose** - PostgreSQL + MongoDB + Backend

### v1.0.0 - Initial Release
- ✅ Arquitectura base Spring Boot 4.0.0
- ✅ Integración PostgreSQL y MongoDB
- ✅ Swagger/OpenAPI documentation
- ✅ Docker deployment

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/amazing-feature`)
3. Commit tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📄 Licencia

MIT License - Ver `LICENSE` para detalles

## 📧 Contacto

Luis Quijada Munoz  
📧 luisquijadaduoc@gmail.com  
🔗 [GitHub](https://github.com/lquijadaduoc)

---

**Última actualización**: 2026-01-10  
**Versión**: v2.0.0  
**Estado**: ✅ Production Ready  
**Servidor**: http://168.197.50.14:8080  
**Swagger UI**: http://168.197.50.14:8080/swagger-ui/index.html
