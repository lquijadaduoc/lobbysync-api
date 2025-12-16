# LobbySync API - Manual de Integración

## Tabla de Contenidos
1. [Introducción](#introducción)
2. [Arquitectura](#arquitectura)
3. [Autenticación](#autenticación)
4. [Módulos y Endpoints](#módulos-y-endpoints)
5. [Ejemplos de Uso](#ejemplos-de-uso)
6. [Códigos de Error](#códigos-de-error)
7. [Entornos](#entornos)

---

## Introducción

LobbySync es una API REST completa para la gestión integral de edificios residenciales y comerciales. Proporciona funcionalidades para administración de usuarios, control de acceso, finanzas y mantenimiento.

### Tecnologías
- **Framework**: Spring Boot 4.0.0
- **Lenguaje**: Java 17
- **Bases de Datos**: 
  - PostgreSQL 15 (entidades relacionales)
  - MongoDB (logs y documentos flexibles)
- **Autenticación**: Firebase Authentication (JWT)
- **Documentación**: OpenAPI 3.0 (Swagger)

---

## Arquitectura

### Base de Datos Híbrida

#### PostgreSQL (SQL)
Almacena entidades estructuradas:
- Buildings (Edificios)
- Units (Unidades/Departamentos)
- Users (Usuarios)
- Invitations (Invitaciones QR)
- Bills (Facturas de gastos comunes)
- Payments (Pagos)

#### MongoDB (NoSQL)
Almacena documentos flexibles:
- AccessLog (Registros de acceso)
- Assets (Activos del edificio)
- MaintenanceTickets (Tickets de mantenimiento)

### Patrones de Diseño
- **Repository Pattern**: Separación de lógica de acceso a datos
- **Service Layer**: Lógica de negocio encapsulada
- **DTO Pattern**: Objetos de transferencia para requests/responses
- **Global Exception Handling**: Manejo centralizado de errores

---

## Autenticación

La API utiliza Firebase Authentication con tokens JWT.

### Headers Requeridos
```http
Authorization: Bearer <FIREBASE_JWT_TOKEN>
Content-Type: application/json
```

### Obtener Token

1. **Frontend Web/Mobile**: Usar Firebase SDK
```javascript
// JavaScript example
import { getAuth, signInWithEmailAndPassword } from "firebase/auth";

const auth = getAuth();
const userCredential = await signInWithEmailAndPassword(auth, email, password);
const token = await userCredential.user.getIdToken();
```

2. **Testing**: Generar token desde Firebase Console o usar herramientas de desarrollo

### Endpoints sin Autenticación
- `GET /actuator/health` - Health check
- `GET /swagger-ui.html` - Documentación Swagger

---

## Módulos y Endpoints

### 1. Core Module - Edificios y Unidades

#### Buildings (Edificios)

##### Crear Edificio
```http
POST /api/v1/buildings
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Edificio Central",
  "address": "Av. Principal 123",
  "isActive": true
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "name": "Edificio Central",
  "address": "Av. Principal 123",
  "isActive": true
}
```

##### Listar Edificios
```http
GET /api/v1/buildings
Authorization: Bearer <token>
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Edificio Central",
    "address": "Av. Principal 123",
    "isActive": true
  }
]
```

##### Obtener Edificio por ID
```http
GET /api/v1/buildings/{id}
Authorization: Bearer <token>
```

#### Units (Unidades)

##### Crear Unidad
```http
POST /api/v1/units
Content-Type: application/json
Authorization: Bearer <token>

{
  "unitNumber": "101",
  "buildingId": 1,
  "aliquot": 5.5,
  "isActive": true
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "unitNumber": "101",
  "buildingId": 1,
  "aliquot": 5.5,
  "isActive": true
}
```

##### Listar Unidades por Edificio
```http
GET /api/v1/buildings/{buildingId}/units
Authorization: Bearer <token>
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "unitNumber": "101",
    "buildingId": 1,
    "aliquot": 5.5,
    "isActive": true
  }
]
```

---

### 2. Access Control Module - Control de Acceso

#### Invitations (Invitaciones QR)

##### Crear Invitación
```http
POST /api/invitations
Content-Type: application/json
Authorization: Bearer <token>

{
  "guestName": "Juan Pérez",
  "guestRut": "12345678-9",
  "unitId": 1,
  "buildingId": 1,
  "validUntil": "2025-12-31T23:59:59"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "guestName": "Juan Pérez",
  "guestRut": "12345678-9",
  "qrToken": "bb4c68c5-8dfc-4809-8ed0-78ebb134ee23",
  "unitId": 1,
  "buildingId": 1,
  "validUntil": "2025-12-31T23:59:59",
  "status": "ACTIVE",
  "valid": true
}
```

##### Listar Invitaciones Activas
```http
GET /api/invitations/active?buildingId=1
Authorization: Bearer <token>
```

#### QR Entry (Entrada con QR)

##### Procesar Entrada QR
```http
POST /api/access/qr
Content-Type: application/json
Authorization: Bearer <token>

{
  "token": "bb4c68c5-8dfc-4809-8ed0-78ebb134ee23",
  "buildingId": 1
}
```

**Response 200 OK:**
```json
{
  "invitationId": 1,
  "guestName": "Juan Pérez",
  "guestRut": "12345678-9",
  "unitId": 1,
  "entryTime": "2025-12-16T01:43:49",
  "message": "Acceso autorizado. Bienvenido/a Juan Pérez"
}
```

**Note**: El QR se marca como USED (burn mechanism) después de ser utilizado.

#### Delivery Entry (Registro de Entregas)

##### Registrar Entrega con Foto
```http
POST /api/access/delivery
Content-Type: multipart/form-data
Authorization: Bearer <token>

deliveryPerson: "Chilexpress"
recipientRut: "12345678-9"
unitId: 1
buildingId: 1
photo: [archivo imagen]
notes: "Paquete pequeño"
```

**Response 200 OK:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "deliveryPerson": "Chilexpress",
  "recipientRut": "12345678-9",
  "unitId": 1,
  "buildingId": 1,
  "photoUrl": "/uploads/photos/2025-12-16_uuid.jpg",
  "notes": "Paquete pequeño",
  "timestamp": "2025-12-16T10:30:00"
}
```

##### Consultar Logs de Acceso
```http
GET /api/access/logs?buildingId=1&startDate=2025-12-01&endDate=2025-12-31
Authorization: Bearer <token>
```

---

### 3. Finance Module - Gestión Financiera

#### Bills (Gastos Comunes)

##### Generar Gastos Comunes
```http
POST /api/finance/generate
Content-Type: application/json
Authorization: Bearer <token>

{
  "buildingId": 1,
  "month": 12,
  "year": 2025,
  "baseAmount": 100000
}
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "unitId": 1,
    "amount": 5500.00,
    "amountPaid": 0,
    "month": 12,
    "year": 2025,
    "status": "PENDING",
    "dueDate": "2025-12-31",
    "description": null,
    "remainingAmount": 5500.00,
    "createdAt": "2025-12-16T01:57:09",
    "updatedAt": "2025-12-16T01:57:09"
  }
]
```

**Cálculo**: El monto se distribuye según la alícuota de cada unidad.
- Unidad con alícuota 5.5% → $100,000 × 5.5% = $5,500

##### Consultar Deuda de Unidad
```http
GET /api/finance/units/{unitId}/debt
Authorization: Bearer <token>
```

**Response 200 OK:**
```json
{
  "unitId": 1,
  "unitNumber": "101",
  "totalDebt": 5500.00,
  "pendingBills": 1
}
```

##### Listar Facturas por Unidad
```http
GET /api/finance/bills?unitId=1&status=PENDING
Authorization: Bearer <token>
```

#### Payments (Pagos)

##### Registrar Pago
```http
POST /api/finance/payments
Content-Type: application/json
Authorization: Bearer <token>

{
  "billId": 1,
  "amount": 3000,
  "paymentMethod": "TRANSFER",
  "reference": "TRX12345"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "billId": 1,
  "unitId": 1,
  "amount": 3000,
  "paymentMethod": "TRANSFER",
  "transactionReference": "TRX12345",
  "notes": null,
  "paymentDate": "2025-12-16T01:57:23"
}
```

**Comportamiento Automático**:
- Si pago parcial: Bill cambia a `PARTIAL`
- Si pago completo: Bill cambia a `PAID`
- La deuda se recalcula automáticamente

##### Listar Pagos
```http
GET /api/finance/payments?unitId=1
Authorization: Bearer <token>
```

---

### 4. Maintenance Module - Mantenimiento

#### Assets (Activos)

##### Crear Activo
```http
POST /api/assets
Content-Type: application/json
Authorization: Bearer <token>

{
  "buildingId": 1,
  "name": "Ascensor Principal",
  "category": "ELEVATOR",
  "status": "OPERATIONAL",
  "specifications": {
    "brand": "Otis",
    "capacity": "8 personas",
    "lastMaintenance": "2025-10-15"
  }
}
```

**Response 201 Created:**
```json
{
  "id": "6940bc8ebd3c98a4b5949551",
  "buildingId": 1,
  "name": "Ascensor Principal",
  "status": "OPERATIONAL",
  "specifications": {
    "brand": "Otis",
    "capacity": "8 personas",
    "lastMaintenance": "2025-10-15"
  },
  "createdAt": "2025-12-16T01:57:34",
  "updatedAt": "2025-12-16T01:57:34"
}
```

**Note**: `specifications` es un campo flexible (Map) para almacenar cualquier atributo personalizado.

##### Listar Activos por Edificio
```http
GET /api/assets/building/{buildingId}
Authorization: Bearer <token>
```

##### Filtrar Activos por Estado
```http
GET /api/assets/building/{buildingId}/status/{status}
Authorization: Bearer <token>
```

Valores de status: `OPERATIONAL`, `MAINTENANCE`, `OUT_OF_SERVICE`

#### Maintenance Tickets (Tickets de Mantenimiento)

##### Crear Ticket
```http
POST /api/assets/{assetId}/ticket
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Ruido extraño en ascensor",
  "description": "Se escucha un ruido metálico al subir al piso 5",
  "priority": "HIGH",
  "reportedBy": "Juan Pérez - Depto 101"
}
```

**Response 201 Created:**
```json
{
  "id": "6940be6a9f13dcf355e065e5",
  "assetId": "6940bc8ebd3c98a4b5949551",
  "title": "Ruido extraño en ascensor",
  "description": "Se escucha un ruido metálico al subir al piso 5",
  "priority": "HIGH",
  "status": "OPEN",
  "reportedBy": "Juan Pérez - Depto 101",
  "reportedDate": "2025-12-16T02:05:30",
  "createdAt": "2025-12-16T02:05:30",
  "updatedAt": "2025-12-16T02:05:30"
}
```

##### Actualizar Estado de Ticket
```http
PUT /api/tickets/{ticketId}/status
Content-Type: application/json
Authorization: Bearer <token>

{
  "status": "IN_PROGRESS",
  "assignedTo": 999
}
```

**Response 200 OK:**
```json
{
  "id": "6940be6a9f13dcf355e065e5",
  "assetId": "6940bc8ebd3c98a4b5949551",
  "title": "Ruido extraño en ascensor",
  "status": "IN_PROGRESS",
  "assignedTo": 999,
  "updatedAt": "2025-12-16T02:05:41"
}
```

Estados disponibles:
- `OPEN`: Recién creado
- `IN_PROGRESS`: En atención
- `RESOLVED`: Resuelto
- `CLOSED`: Cerrado

Prioridades:
- `LOW`: Baja
- `MEDIUM`: Media
- `HIGH`: Alta
- `CRITICAL`: Crítica

##### Listar Tickets por Activo
```http
GET /api/assets/{assetId}/tickets
Authorization: Bearer <token>
```

##### Resolver Ticket
```http
PUT /api/tickets/{ticketId}/resolve
Content-Type: application/json
Authorization: Bearer <token>

{
  "resolution": "Se reemplazó el rodamiento del motor",
  "status": "RESOLVED"
}
```

---

## Ejemplos de Uso

### Caso 1: Flujo Completo de Invitación QR

```bash
# 1. Crear invitación
curl -X POST http://localhost:8080/api/invitations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "guestName": "María González",
    "guestRut": "98765432-1",
    "unitId": 1,
    "buildingId": 1,
    "validUntil": "2025-12-20T23:59:59"
  }'

# Response: qrToken = "abc123..."

# 2. Visitante escanea QR y conserje procesa entrada
curl -X POST http://localhost:8080/api/access/qr \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "token": "abc123...",
    "buildingId": 1
  }'

# Response: Acceso autorizado + log guardado en MongoDB
```

### Caso 2: Generación y Pago de Gastos Comunes

```bash
# 1. Generar gastos del mes
curl -X POST http://localhost:8080/api/finance/generate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "buildingId": 1,
    "month": 12,
    "year": 2025,
    "baseAmount": 500000
  }'

# 2. Consultar deuda de unidad específica
curl -X GET "http://localhost:8080/api/finance/units/1/debt" \
  -H "Authorization: Bearer $TOKEN"

# 3. Registrar pago parcial
curl -X POST http://localhost:8080/api/finance/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "billId": 1,
    "amount": 15000,
    "paymentMethod": "TRANSFER",
    "reference": "BANCO123"
  }'

# 4. Verificar deuda actualizada
curl -X GET "http://localhost:8080/api/finance/units/1/debt" \
  -H "Authorization: Bearer $TOKEN"
```

### Caso 3: Gestión de Mantenimiento

```bash
# 1. Crear activo (ascensor)
curl -X POST http://localhost:8080/api/assets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "buildingId": 1,
    "name": "Ascensor Norte",
    "category": "ELEVATOR",
    "status": "OPERATIONAL",
    "specifications": {
      "brand": "Schindler",
      "year": "2020",
      "floors": "15"
    }
  }'

# Response: id = "mongoID123"

# 2. Reportar problema
curl -X POST "http://localhost:8080/api/assets/mongoID123/ticket" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Ascensor atascado en piso 7",
    "description": "No responde a llamados",
    "priority": "CRITICAL",
    "reportedBy": "Conserje - Turno Mañana"
  }'

# Response: ticketId = "ticketMongoID"

# 3. Técnico toma el caso
curl -X PUT "http://localhost:8080/api/tickets/ticketMongoID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS",
    "assignedTo": 456
  }'

# 4. Resolver ticket
curl -X PUT "http://localhost:8080/api/tickets/ticketMongoID/resolve" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "resolution": "Se reseteó el sistema. Pruebas OK",
    "status": "RESOLVED"
  }'
```

---

## Códigos de Error

### Errores HTTP Comunes

| Código | Descripción | Causa Común |
|--------|-------------|-------------|
| 400 | Bad Request | JSON malformado, campos faltantes |
| 401 | Unauthorized | Token inválido o expirado |
| 403 | Forbidden | Sin permisos para el recurso |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | QR ya usado, duplicados |
| 500 | Internal Server Error | Error del servidor |

### Formato de Respuesta de Error

```json
{
  "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
  "error": "Bad Request",
  "message": "Validation failed for object='invitation'",
  "status": 400,
  "timestamp": "2025-12-16T02:30:00"
}
```

### Errores Específicos del Dominio

#### Invitaciones
```json
{
  "error": "Invalid QR Code",
  "message": "La invitación ya fue utilizada o expiró",
  "status": 409
}
```

#### Finanzas
```json
{
  "error": "Insufficient Payment",
  "message": "El monto del pago excede la deuda pendiente",
  "status": 400
}
```

---

## Entornos

### Development
```
URL: http://localhost:8080
Base de Datos: Local Docker
- PostgreSQL: localhost:5432
- MongoDB: localhost:27017
Firebase: Proyecto de desarrollo
```

### Production
```
URL: https://api.lobbysync.cl
Base de Datos: Cloud
Firebase: Proyecto de producción
```

---

## Documentación Interactiva

### Swagger UI
```
URL: http://localhost:8080/swagger-ui.html
```

Proporciona:
- Listado completo de endpoints
- Esquemas de requests/responses
- Pruebas interactivas
- Autenticación integrada

### OpenAPI Spec
```
URL: http://localhost:8080/v3/api-docs
```

Exportar especificación para generar clientes automáticamente.

---

## Consideraciones de Seguridad

1. **Tokens JWT**: Expiración recomendada de 1 hora
2. **HTTPS**: Obligatorio en producción
3. **Rate Limiting**: Implementar en producción
4. **CORS**: Configurado según dominios permitidos
5. **Logs**: No almacenar información sensible

---

## Soporte

Para dudas o problemas:
- **Email**: soporte@lobbysync.cl
- **GitHub**: https://github.com/lquijadaduoc/lobbysync-api
- **Documentación**: http://localhost:8080/swagger-ui.html

---

## Changelog

### v1.0.0 (2025-12-16)
- ✅ Core Module: Buildings, Units, Users
- ✅ Access Control: QR Entry, Delivery Entry
- ✅ Finance Module: Bills, Payments, Debt tracking
- ✅ Maintenance Module: Assets, Tickets
- ✅ Firebase Authentication
- ✅ Hybrid Database (PostgreSQL + MongoDB)
- ✅ OpenAPI/Swagger Documentation
