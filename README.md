# 🏢 LobbySync Backend API

Plataforma SaaS para gestión integral de edificios residenciales y comerciales, con integración Firebase para autenticación y gestión completa de usuarios.

## 🚀 Características Principales

- **✅ Gestión Completa de Usuarios con Firebase**
  - CRUD completo (Crear, Leer, Actualizar, Eliminar)
  - Integración bidireccional con Firebase Authentication  
  - Cambio de contraseñas en Firebase
  - Asignación de departamentos a residentes
  - Sincronización automática PostgreSQL ↔ Firebase
  
- **🏢 Gestión de Edificios**: CRUD completo de propiedades residenciales y comerciales
- **🚪 Control de Acceso**: Registro de entrada/salida con timestamps
- **📦 Gestión de Paquetería**: Seguimiento de entregas y notificaciones
- **💰 Administración Financiera**: Generación y seguimiento de facturas
- **🛠️ Gestión de Activos**: Registro de activos y tickets de mantenimiento
- **🎫 Sistema de Reservas**: Áreas comunes con aprobación automática
- **👥 Gestión de Visitantes**: Invitaciones y control de acceso
- **🔐 Autenticación Firebase**: Integración completa con Firebase Admin SDK
- **📊 Base de datos híbrida**: PostgreSQL (datos transaccionales) + MongoDB (logs y eventos)
- **📚 Documentación Swagger**: OpenAPI 3.0 con UI interactiva
- **🐳 Docker**: Despliegue containerizado con compose

## 🏗️ Arquitectura

```
┌──────────────────────────┐
│   Firebase Auth          │
│  (Client SDK + Admin)    │
└──────────┬───────────────┘
           │ ID Token
           ▼
┌──────────────────────────────────┐
│  Spring Boot 4.0.0 Backend       │
│  Java 17 + Hibernate 7.1         │
├──────────────────────────────────┤
│  ✓ REST API Endpoints            │
│  ✓ Firebase Token Validation     │
│  ✓ User Management (CRUD)        │
│  ✓ Role-Based Access Control     │
│  ✓ Swagger/OpenAPI 3.0           │
│  ✓ JWT Authentication            │
└──────────┬───────────────────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
┌─────────┐  ┌──────────┐
│PostgreSQL│  │MongoDB   │
│  15.15  │  │  Latest  │
│ ┌─────┐ │  │ ┌──────┐ │
│ │Users│ │  │ │Logs  │ │
│ │Units│ │  │ │Events│ │
│ │Bills│ │  │ └──────┘ │
│ └─────┘ │  │          │
└─────────┘  └──────────┘
```

## 📋 Requisitos Previos

- **Docker & Docker Compose** (recomendado)
- **Java 17+** (para desarrollo local)
- **Maven 3.9+**
- **Cuenta Firebase** con proyecto configurado
- **PostgreSQL 15** (si no usas Docker)
- **MongoDB** (si no usas Docker)

## 🔧 Instalación & Despliegue

### Con Docker (Recomendado - Producción)

```bash
# Clonar repositorio
git clone https://github.com/lquijadaduoc/lobbysync-api.git
cd lobbysync-api

# Configurar Firebase (ver sección Firebase)
# Copiar serviceAccountKey.json a la raíz del proyecto

# Iniciar servicios
docker-compose up -d

# Verificar estado
docker ps

# Ver logs
docker logs lobbysync_backend -f
```

### Desarrollo Local

```bash
# Instalar dependencias
mvn clean install

# Configurar variables de entorno
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lobbysync
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres_password
export SPRING_MONGODB_HOST=localhost
export SPRING_MONGODB_PORT=27017

# Ejecutar aplicación
mvn spring-boot:run
```

### Deployment a Producción VPS

```bash
# Usar script automatizado
cd lobbysync-api
.\deploy-produccion-usuarios.ps1  # Windows
# o
./deploy-vps.sh  # Linux

# El script automáticamente:
# 1. Compila con Maven
# 2. Sube JAR al VPS
# 3. Reinicia contenedores Docker
# 4. Verifica logs
```

## 🔐 Configuración Firebase

### Paso 1: Crear Proyecto Firebase

1. Ir a [Firebase Console](https://console.firebase.google.com)
2. Crear nuevo proyecto: `lobbysync-91db0`
3. Habilitar **Authentication** → **Email/Password**

### Paso 2: Obtener Service Account Key

1. Project Settings → **Service Accounts**
2. Click **Generate New Private Key**
3. Guardar como `serviceAccountKey.json` en la raíz del proyecto
4. **IMPORTANTE**: NO commitear a Git (ya está en .gitignore)

### Paso 3: Configurar Backend

```bash
# El archivo debe estar en:
/root/lobbysync-api/serviceAccountKey.json  # Producción
./serviceAccountKey.json                     # Desarrollo local
```

### Paso 4: Configurar Frontend

```javascript
// src/config/firebase.js
const firebaseConfig = {
  apiKey: "AIzaSyD...",
  authDomain: "lobbysync-91db0.firebaseapp.com",
  projectId: "lobbysync-91db0",
  storageBucket: "lobbysync-91db0.firebasestorage.app",
  messagingSenderId: "...",
  appId: "..."
};
```

## 📚 API Endpoints

### 🔐 Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Login con email/password Firebase |
| `POST` | `/api/auth/refresh` | Refrescar token JWT |
| `POST` | `/api/auth/logout` | Cerrar sesión |

### 👥 Gestión de Usuarios

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| `GET` | `/api/v1/users` | Listar todos los usuarios | ADMIN |
| `GET` | `/api/v1/users/{id}` | Obtener usuario por ID | ADMIN |
| `POST` | `/api/v1/users` | **Crear usuario en Firebase + PostgreSQL** | ADMIN |
| `PUT` | `/api/v1/users/{id}` | **Actualizar usuario** | ADMIN |
| `DELETE` | `/api/v1/users/{id}` | **Eliminar usuario (Firebase + DB)** | ADMIN |
| `POST` | `/api/v1/users/{id}/change-password` | **Cambiar contraseña en Firebase** | ADMIN |
| `GET` | `/api/v1/users/firebase/{uid}` | Obtener usuario por Firebase UID | ANY |

#### Ejemplo: Crear Usuario

```bash
POST /api/v1/users
Content-Type: application/json

{
  "email": "nuevo@lobbysync.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "RESIDENT",
  "phone": "+56912345678",
  "unitId": 1  // Solo si role = RESIDENT
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente en Firebase y PostgreSQL",
  "userId": 10,
  "firebaseUid": "xxxxxxxxxxx",
  "email": "nuevo@lobbysync.com",
  "role": "RESIDENT"
}
```

### 🏢 Edificios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/buildings` | Listar edificios |
| `GET` | `/api/v1/buildings/{id}` | Obtener edificio |
| `POST` | `/api/v1/buildings` | Crear edificio |
| `PUT` | `/api/v1/buildings/{id}` | Actualizar edificio |
| `DELETE` | `/api/v1/buildings/{id}` | Eliminar edificio |
| `GET` | `/api/v1/buildings/{id}/units` | Unidades del edificio |

### 🏠 Unidades

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/units` | Listar todas las unidades |
| `GET` | `/api/v1/units/{id}` | Obtener unidad por ID |
| `POST` | `/api/v1/units` | Crear unidad |
| `PUT` | `/api/v1/units/{id}` | Actualizar unidad |
| `DELETE` | `/api/v1/units/{id}` | Eliminar unidad |

### 📦 Paquetería

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/parcels` | Listar paquetes |
| `GET` | `/api/parcels/status/{status}` | Paquetes por estado |
| `POST` | `/api/parcels` | Registrar nuevo paquete |
| `PATCH` | `/api/parcels/{id}/deliver` | Marcar como entregado |

### 🎫 Reservas de Áreas Comunes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/reservations/common-areas` | Áreas disponibles |
| `GET` | `/api/reservations/my-upcoming` | Mis próximas reservas |
| `GET` | `/api/reservations/my-reservations` | Todas mis reservas |
| `POST` | `/api/reservations` | Crear reserva |
| `DELETE` | `/api/reservations/{id}` | Cancelar reserva |
| `PATCH` | `/api/reservations/{id}/status` | Actualizar estado |
| `POST` | `/api/reservations/{id}/approve` | Aprobar/rechazar reserva |

### 👥 Visitantes e Invitaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/invitations` | Listar invitaciones |
| `POST` | `/api/invitations` | Crear invitación |
| `PATCH` | `/api/invitations/{id}/check-in` | Registrar entrada |
| `PATCH` | `/api/invitations/{id}/check-out` | Registrar salida |

### 🛠️ Tickets de Mantenimiento

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/tickets` | Listar tickets |
| `POST` | `/api/tickets` | Crear ticket |
| `PATCH` | `/api/tickets/{id}/status` | Actualizar estado |

### 💰 Finanzas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/bills` | Listar facturas |
| `POST` | `/api/v1/finance/bills/generate` | Generar facturas |
| `POST` | `/api/v1/bills/{id}/pay` | Registrar pago |

## 🔑 Roles y Permisos

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Acceso completo a todos los endpoints, gestión de usuarios, edificios, finanzas |
| **CONCIERGE** | Gestión de paquetes, visitantes, tickets de mantenimiento |
| **RESIDENT** | Ver mis paquetes, crear invitaciones, reservar áreas comunes |
| **FINANCE** | Gestión de facturas, pagos, reportes financieros |

## 📖 Documentación Swagger

Una vez desplegado, accede a:

- **Swagger UI**: http://168.197.50.14:8080/swagger-ui.html
- **OpenAPI JSON**: http://168.197.50.14:8080/v3/api-docs

### Funcionalidades de Swagger:
- 📋 Lista completa de endpoints
- 🧪 Probar endpoints desde el navegador
- 📝 Esquemas de request/response
- 🔐 Autenticación Bearer Token
- 📚 Documentación inline de parámetros

## 📝 Changelog

### v1.1.0 (2026-01-20) ✨ NUEVO
- ✅ **Gestión Completa de Usuarios**
  - Endpoint `POST /api/v1/users` para crear usuarios con Firebase
  - Endpoint `PUT /api/v1/users/{id}` para actualizar usuarios
  - Endpoint `DELETE /api/v1/users/{id}` para eliminar (Firebase + DB)
  - Endpoint `POST /api/v1/users/{id}/change-password` para cambiar contraseñas
  - Asignación automática de departamentos a residentes (unitId)
  - Sincronización bidireccional PostgreSQL ↔ Firebase
- 🛠️ **Mejoras**
  - UserService refactorizado con Unit repository injection
  - DTOs actualizados: UpdateUserRequest, ChangePasswordRequest
  - Script de deployment mejorado con nombre correcto de contenedores

### v1.0.0 (2026-01-15)
- ✅ Sistema base con autenticación Firebase
- ✅ Gestión de edificios y unidades
- ✅ Sistema de reservas de áreas comunes
- ✅ Paquetería y visitantes
- ✅ Tickets de mantenimiento

## 📄 Licencia

Este proyecto es privado y está bajo desarrollo para uso interno.

## 👥 Equipo

- **Backend Lead**: Sebastian
- **Firebase Integration**: Sebastian
- **DevOps**: Sebastian
