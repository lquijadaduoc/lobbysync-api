# 🧪 LobbySync API - Guía de Testing de Endpoints

## Endpoints Disponibles - Quick Reference

### 1. Usuarios (Users)
```bash
# Obtener todos los usuarios
GET http://localhost:8080/api/v1/users

# Obtener usuario específico
GET http://localhost:8080/api/v1/users/{id}

# Crear nuevo usuario
POST http://localhost:8080/api/v1/users
Content-Type: application/json
{
  "email": "usuario@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "RESIDENT"
}

# Actualizar usuario
PUT http://localhost:8080/api/v1/users/{id}

# Eliminar usuario
DELETE http://localhost:8080/api/v1/users/{id}
```

### 2. Edificios (Buildings)
```bash
# Obtener todos los edificios
GET http://localhost:8080/api/v1/buildings

# Obtener edificio específico
GET http://localhost:8080/api/v1/buildings/{id}

# Crear nuevo edificio
POST http://localhost:8080/api/v1/buildings
Content-Type: application/json
{
  "name": "Edificio Centro",
  "address": "Calle Principal 123",
  "city": "Santiago",
  "country": "Chile"
}
```

### 3. Unidades (Units)
```bash
# Obtener todas las unidades
GET http://localhost:8080/api/v1/units

# Obtener unidades de un edificio
GET http://localhost:8080/api/v1/buildings/{buildingId}/units

# Crear nueva unidad
POST http://localhost:8080/api/v1/units
Content-Type: application/json
{
  "number": "301",
  "floor": 3,
  "buildingId": 1,
  "type": "APARTMENT"
}
```

### 4. Tickets de Mantenimiento
```bash
# Obtener todos los tickets
GET http://localhost:8080/api/tickets

# Obtener ticket específico
GET http://localhost:8080/api/tickets/{id}

# Crear nuevo ticket
POST http://localhost:8080/api/tickets
Content-Type: application/json
{
  "title": "Reparación de tuberías",
  "description": "Fuga en baño",
  "priority": "HIGH",
  "unitId": 1
}

# Cambiar estado del ticket
PATCH http://localhost:8080/api/tickets/{id}/status
Content-Type: application/json
{
  "status": "IN_PROGRESS"
}

# Asignar ticket
PATCH http://localhost:8080/api/tickets/{id}/assign
Content-Type: application/json
{
  "assignedToId": 2
}
```

### 5. Documentos
```bash
# Obtener todos los documentos
GET http://localhost:8080/api/documents

# Obtener documento específico
GET http://localhost:8080/api/documents/{id}

# Obtener documentos por categoría
GET http://localhost:8080/api/documents/category/{category}

# Obtener documentos públicos
GET http://localhost:8080/api/documents/public

# Crear nuevo documento
POST http://localhost:8080/api/documents
Content-Type: multipart/form-data
- file: [archivo a subir]
- category: "REGULATIONS"
- title: "Reglamento del edificio"
```

### 6. Notificaciones (Broadcasts)
```bash
# Obtener todas las notificaciones
GET http://localhost:8080/api/broadcasts

# Obtener notificaciones activas
GET http://localhost:8080/api/broadcasts/active

# Obtener notificación específica
GET http://localhost:8080/api/broadcasts/{id}

# Crear nueva notificación
POST http://localhost:8080/api/broadcasts
Content-Type: application/json
{
  "title": "Corte de agua programado",
  "content": "El próximo viernes habrá corte de agua",
  "priority": "MEDIUM",
  "targetRole": "RESIDENT"
}

# Eliminar notificación
DELETE http://localhost:8080/api/broadcasts/{id}

# Obtener estadísticas de notificaciones
GET http://localhost:8080/api/broadcasts/stats
```

### 7. Mascotas (Pets)
```bash
# Obtener mascotas de un residente
GET http://localhost:8080/api/home/pets

# Obtener mascota específica
GET http://localhost:8080/api/home/pets/{id}

# Crear nueva mascota
POST http://localhost:8080/api/home/pets
Content-Type: application/json
{
  "name": "Firulais",
  "type": "DOG",
  "breed": "Golden Retriever",
  "weight": 25.5
}

# Actualizar mascota
PUT http://localhost:8080/api/home/pets/{id}

# Eliminar mascota
DELETE http://localhost:8080/api/home/pets/{id}
```

### 8. Vehículos (Vehicles)
```bash
# Obtener vehículos de un residente
GET http://localhost:8080/api/home/vehicles

# Crear nuevo vehículo
POST http://localhost:8080/api/home/vehicles
Content-Type: application/json
{
  "licensePlate": "ABC-1234",
  "brand": "Toyota",
  "model": "Corolla",
  "color": "Blanco"
}

# Actualizar vehículo
PUT http://localhost:8080/api/home/vehicles/{id}

# Eliminar vehículo
DELETE http://localhost:8080/api/home/vehicles/{id}
```

### 9. Familia (Family Members)
```bash
# Obtener miembros de la familia
GET http://localhost:8080/api/home/family

# Crear nuevo miembro de la familia
POST http://localhost:8080/api/home/family
Content-Type: application/json
{
  "firstName": "María",
  "lastName": "Pérez",
  "relationship": "SPOUSE",
  "dateOfBirth": "1990-05-15"
}
```

### 10. Registro de Acceso (Logbook)
```bash
# Obtener registro de acceso
GET http://localhost:8080/logbook

# Obtener entrada específica
GET http://localhost:8080/logbook/{id}

# Crear nueva entrada
POST http://localhost:8080/logbook
Content-Type: application/json
{
  "visitorName": "John Smith",
  "purpose": "Entrega de paquete",
  "unitId": 1
}
```

### 11. Lista Blanca (Whitelist)
```bash
# Obtener lista blanca
GET http://localhost:8080/api/whitelist

# Obtener entrada específica
GET http://localhost:8080/api/whitelist/{id}

# Agregar a lista blanca
POST http://localhost:8080/api/whitelist
Content-Type: application/json
{
  "name": "Carlos García",
  "email": "carlos@example.com",
  "reason": "Proveedor frecuente"
}

# Actualizar entrada
PUT http://localhost:8080/api/whitelist/{id}

# Eliminar entrada
DELETE http://localhost:8080/api/whitelist/{id}
```

### 12. Validación de Visitas
```bash
# Validar acceso de visitante
POST http://localhost:8080/api/visits/validate
Content-Type: application/json
{
  "visitorId": 1,
  "accessCode": "ABC123"
}
```

### 13. Invitaciones
```bash
# Obtener invitaciones
GET http://localhost:8080/api/invitations

# Obtener invitación específica
GET http://localhost:8080/api/invitations/{id}

# Crear invitación
POST http://localhost:8080/api/invitations
Content-Type: application/json
{
  "guestEmail": "guest@example.com",
  "guestName": "María López",
  "unitId": 1
}

# Aceptar invitación
PATCH http://localhost:8080/api/invitations/{id}/accept

# Rechazar invitación
PATCH http://localhost:8080/api/invitations/{id}/reject
```

## Ejemplos de Testing con PowerShell

### Test básico de GET
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/buildings" -UseBasicParsing
Write-Host "Status Code: $($response.StatusCode)"
$data = $response.Content | ConvertFrom-Json
Write-Host "Count: $($data.Count)"
```

### Test de POST
```powershell
$body = @{
    name = "Edificio Nuevo"
    address = "Calle 456"
    city = "Valparaíso"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/buildings" `
  -Method POST `
  -Body $body `
  -ContentType "application/json" `
  -UseBasicParsing

Write-Host "Status: $($response.StatusCode)"
$response.Content | ConvertFrom-Json
```

## Acceso a Documentación Interactiva

### Swagger UI
Abre en tu navegador:
```
http://localhost:8080/swagger-ui.html
```

Desde aquí puedes:
- Ver todos los endpoints disponibles
- Probar endpoints de forma interactiva
- Ver esquemas de request y response
- Descargar especificación OpenAPI

### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

Copia este JSON en [editor.swagger.io](https://editor.swagger.io) para obtener documentación interactiva.

## Notas Importantes

1. **Base de datos vacía**: Los endpoints deben ser probados con datos nuevos
2. **Autenticación**: Algunos endpoints pueden requerir autenticación
3. **CORS**: Configurado para permitir requests desde el frontend
4. **Validación**: Algunos campos pueden ser obligatorios según el endpoint
5. **Rate limiting**: No configurado por defecto

## Comandos Útiles

```bash
# Ver logs en tiempo real
docker logs -f lobbysync_backend

# Acceder a la base de datos PostgreSQL
docker exec -it postgres_db psql -U postgres -d lobbysync

# Acceder a MongoDB
docker exec -it mongo_db mongosh

# Reiniciar API
docker restart lobbysync_backend

# Ver estado de contenedores
docker ps
```

---

**Última actualización**: 15 de Enero de 2026
**Estado**: ✅ Completamente funcional
