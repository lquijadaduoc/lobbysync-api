# Nuevos Endpoints Backend - LobbySync API

## Resumen de Implementación

Se han desarrollado **4 nuevos controladores** con sus respectivos modelos, servicios y repositorios para completar todas las funcionalidades del sistema.

---

## 📋 1. HomeController - `/api/home`
**Gestión de familia, mascotas y vehículos del hogar**

### Familia
- `GET /api/home/family` - Listar miembros de familia
- `POST /api/home/family` - Crear miembro de familia
- `PUT /api/home/family/{id}` - Actualizar miembro
- `DELETE /api/home/family/{id}` - Eliminar miembro

### Mascotas
- `GET /api/home/pets` - Listar mascotas
- `POST /api/home/pets` - Registrar mascota
- `PUT /api/home/pets/{id}` - Actualizar mascota
- `DELETE /api/home/pets/{id}` - Eliminar mascota

### Vehículos
- `GET /api/home/vehicles` - Listar vehículos
- `POST /api/home/vehicles` - Registrar vehículo
- `PUT /api/home/vehicles/{id}` - Actualizar vehículo
- `DELETE /api/home/vehicles/{id}` - Eliminar vehículo

**Modelos SQL:**
- `FamilyMember` - Tabla `family_members`
- `Pet` - Tabla `pets`
- `Vehicle` - Tabla `vehicles`

**Características:**
- Todos los endpoints obtienen automáticamente el `unitId` del usuario autenticado
- Relación `@ManyToOne` con la entidad `Unit`
- Timestamps automáticos con `@PrePersist` y `@PreUpdate`

---

## 🛡️ 2. WhitelistController - `/api/whitelist`
**Gestión de lista blanca de visitas frecuentes**

### Endpoints
- `GET /api/whitelist` - Listar contactos de lista blanca
- `POST /api/whitelist` - Crear contacto
- `PUT /api/whitelist/{id}` - Actualizar contacto
- `DELETE /api/whitelist/{id}` - Eliminar contacto

**Modelo SQL:**
- `WhitelistContact` - Tabla `whitelist_contacts`

**Campos:**
- `name`, `rut`, `relationship`, `phone`
- `hasPermaAccess` - Indica si tiene acceso permanente
- `notes` - Observaciones adicionales

---

## 📄 3. DocumentController - `/api/documents`
**Biblioteca de documentos del edificio**

### Endpoints
- `GET /api/documents` - Listar documentos públicos
- `GET /api/documents/category/{category}` - Filtrar por categoría
- `GET /api/documents/{id}` - Obtener documento
- `POST /api/documents` - Crear documento
- `PUT /api/documents/{id}` - Actualizar documento
- `DELETE /api/documents/{id}` - Eliminar documento
- `POST /api/documents/{id}/download` - Registrar descarga

**Modelo MongoDB:**
- `Document` - Colección `documents`

**Categorías:**
- `REGLAMENTO` - Reglamento de copropiedad
- `ACTAS` - Actas de asamblea
- `COMUNICADOS` - Comunicados oficiales
- `OTROS` - Otros documentos

**Campos:**
- `title`, `description`, `category`
- `filePath`, `fileName`, `fileType`, `fileSize`
- `buildingId`, `uploadedBy`, `uploadedAt`
- `isPublic`, `downloadCount`

---

## 📢 4. BroadcastController - `/api/admin/broadcasts`
**Comunicación masiva a residentes y conserjes**

### Endpoints
- `GET /api/admin/broadcasts` - Listar broadcasts
- `GET /api/admin/broadcasts/{id}` - Obtener broadcast
- `POST /api/admin/broadcasts` - Enviar broadcast
- `DELETE /api/admin/broadcasts/{id}` - Eliminar broadcast
- `GET /api/admin/broadcasts/stats` - Obtener estadísticas

**Modelo MongoDB:**
- `Broadcast` - Colección `broadcasts`

**Campos:**
- `type` - ANNOUNCEMENT, ALERT, NEWS
- `title`, `message`
- `targetAudience` - ALL, RESIDENTS, CONCIERGES
- `priority` - LOW, NORMAL, HIGH, URGENT
- `sentBy`, `sentAt`, `expiresAt`
- `recipientCount`, `deliveredCount`, `readCount`
- `isActive`

**Estadísticas retornadas:**
```json
{
  "totalSent": 25,
  "deliveryRate": 95.2,
  "readRate": 87.3
}
```

---

## 💰 5. FinanceController - Nuevos Endpoints
**Endpoints adicionales para administración financiera**

### Nuevos Endpoints
- `GET /api/finance/morose-units` - Unidades morosas
- `GET /api/finance/stats` - Estadísticas financieras

**Respuesta de `/morose-units`:**
```json
[
  {
    "unitId": 101,
    "unitNumber": "401",
    "buildingName": "Torre A",
    "monthsOverdue": 3,
    "totalDebt": 225000,
    "unpaidBills": [...]
  }
]
```

**Respuesta de `/stats`:**
```json
{
  "morosityRate": 15.5,
  "totalUnits": 120,
  "moroseUnits": 18,
  "totalCollected": 18500000,
  "totalPending": 2800000,
  "unpaidBillsCount": 42
}
```

---

## 🗄️ Base de Datos

### Nuevas Tablas SQL (PostgreSQL)
1. `family_members` - Miembros de familia
2. `pets` - Mascotas
3. `vehicles` - Vehículos
4. `whitelist_contacts` - Lista blanca

**Script de migración:** `V3__Create_Home_And_Whitelist_Tables.sql`

### Nuevas Colecciones MongoDB
1. `documents` - Biblioteca de documentos
2. `broadcasts` - Mensajes broadcast

---

## 📦 Archivos Creados

### Modelos
- `FamilyMember.java`
- `Pet.java`
- `Vehicle.java`
- `WhitelistContact.java`
- `Document.java` (MongoDB)
- `Broadcast.java` (MongoDB)

### Repositorios
- `FamilyMemberRepository.java`
- `PetRepository.java`
- `VehicleRepository.java`
- `WhitelistContactRepository.java`
- `DocumentRepository.java`
- `BroadcastRepository.java`

### Servicios
- `HomeService.java`
- `WhitelistService.java`
- `DocumentService.java`
- `BroadcastService.java`
- `FinanceService.java` (actualizado)

### Controladores
- `HomeController.java`
- `WhitelistController.java`
- `DocumentController.java`
- `BroadcastController.java`
- `FinanceController.java` (actualizado)

---

## 🚀 Despliegue

### Pasos para Desplegar

1. **Ejecutar migración SQL:**
```bash
# Ejecutar el script de migración en PostgreSQL
psql -U postgres -d lobbysync -f V3__Create_Home_And_Whitelist_Tables.sql
```

2. **Compilar el proyecto:**
```bash
cd lobbysync-api
mvn clean package -DskipTests
```

3. **Rebuild y restart del contenedor Docker:**
```bash
docker-compose down
docker-compose build
docker-compose up -d
```

4. **Verificar que los endpoints estén disponibles:**
```bash
# Swagger UI
http://168.197.50.14:8080/swagger-ui/index.html

# Health check
curl http://168.197.50.14:8080/actuator/health
```

---

## ✅ Estado del Backend

### Endpoints Implementados (100%)

| Funcionalidad | Controller | Endpoints | Estado |
|--------------|-----------|-----------|--------|
| Familia, Mascotas, Vehículos | HomeController | 12 endpoints | ✅ |
| Lista Blanca | WhitelistController | 4 endpoints | ✅ |
| Documentos | DocumentController | 7 endpoints | ✅ |
| Broadcasts | BroadcastController | 5 endpoints | ✅ |
| Finanzas Admin | FinanceController | 2 nuevos | ✅ |

**Total: 30 nuevos endpoints implementados**

---

## 🔐 Seguridad

Todos los endpoints que requieren autenticación:
- Utilizan `Authentication` de Spring Security
- Obtienen el usuario desde Firebase UID
- Validan que el usuario tenga acceso a su propia unidad
- Los endpoints de admin requieren rol `ADMIN` o `SUPER_ADMIN`

---

## 📚 Documentación API

Swagger UI disponible en:
```
http://168.197.50.14:8080/swagger-ui/index.html
```

Todos los endpoints están documentados con:
- `@Operation` - Descripción del endpoint
- `@Tag` - Agrupación por funcionalidad
- DTOs con validaciones

---

## 🧪 Testing

Para probar los endpoints desde el frontend:

1. **MyHome (Familia, Mascotas, Vehículos):**
   - Navegar a `/resident/my-home`
   - Ya no debe mostrar mensaje de "funcionalidad en desarrollo"
   - Debe permitir crear/editar/eliminar

2. **Whitelist:**
   - Navegar a `/resident/whitelist`
   - Debe listar y permitir CRUD de contactos

3. **Documents:**
   - Navegar a `/resident/documents`
   - Debe mostrar documentos públicos por categoría

4. **Broadcast:**
   - Navegar a `/admin/broadcast`
   - Debe permitir enviar mensajes y ver estadísticas

5. **Admin Finances:**
   - Navegar a `/admin/finances`
   - Debe mostrar unidades morosas y estadísticas reales

---

## 📝 Notas Importantes

1. **MongoDB:** Las colecciones se crean automáticamente al insertar el primer documento
2. **Índices:** Ya están definidos en el script de migración SQL
3. **Cascada:** Al eliminar una unidad, se eliminan automáticamente todos sus datos relacionados (familia, mascotas, vehículos, whitelist)
4. **Validaciones:** Los modelos incluyen validaciones básicas con `@Column(nullable = false)`
5. **Timestamps:** Todos los modelos tienen `createdAt` y `updatedAt` automáticos

---

## 🐛 Troubleshooting

**Si hay errores al iniciar:**

1. Verificar que PostgreSQL esté corriendo
2. Verificar que MongoDB esté corriendo
3. Verificar las credenciales en `application.properties`
4. Verificar que las tablas se hayan creado correctamente
5. Revisar logs: `docker logs lobbysync-backend`

**Si hay errores 404:**
- Verificar que el backend esté desplegado
- Verificar la URL base en el frontend
- Revisar Swagger para confirmar endpoints disponibles
