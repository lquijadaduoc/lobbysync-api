# Guía de Recuperación y Sincronización VPS

## 📋 Resumen

El VPS usa **Docker** con volúmenes persistentes. Los datos NO se pierden al reiniciar la máquina. Sin embargo, si necesitas reconstruir desde cero o si hay problemas, sigue esta guía.

## 🔧 Archivos Importantes

### Scripts de Recuperación
- **`fix-vps-remote.ps1`**: Ejecutar desde Windows para recuperar el VPS remotamente
- **`fix-vps.sh`**: Script que se ejecuta en el VPS (copiado automáticamente)
- **`seed-actual.sql`**: Datos de producción (PostgreSQL)
- **`sync-firebase-users.ps1`**: Sincronizar usuarios con Firebase

### Configuración
- **`docker-compose.yml`**: Configuración de servicios Docker
- **Volúmenes persistentes**: 
  - `postgres_data` → Datos de PostgreSQL
  - `mongo_data` → Datos de MongoDB

## 🚀 Recuperación Completa del VPS

### Paso 1: Recuperar Servicios y Base de Datos

```powershell
cd "c:\Users\Sebastian\Desktop\Examen Final\lobbysync-api"
.\fix-vps-remote.ps1
```

**Este script:**
1. ✅ Copia archivos necesarios al VPS
2. ✅ Detiene y limpia contenedores
3. ✅ Reinicia PostgreSQL y MongoDB
4. ✅ Inicia el backend
5. ✅ **CARGA AUTOMÁTICAMENTE** los datos de producción desde `seed-actual.sql`

**Resultado:**
- 3 Edificios
- 12 Unidades
- 5 Áreas Comunes
- 9 Usuarios (PostgreSQL)

### Paso 2: Sincronizar con Firebase

```powershell
.\sync-firebase-users.ps1
```

**Este script:**
1. ✅ Crea usuarios en Firebase Authentication
2. ✅ Vincula con usuarios existentes en PostgreSQL
3. ✅ Verifica que todos los usuarios estén sincronizados

## 🔐 Credenciales de Usuarios

### Administradores
- `admin@lobbysync.cl` / `Admin123!`
- `admin2@lobbysync.cl` / `Admin123!`

### Conserjes
- `conserje1@lobbysync.cl` / `Conserje123!`
- `conserje2@lobbysync.cl` / `Conserje123!`

### Residentes
- `residente1@mail.com` / `Residente123!`
- `residente2@mail.com` / `Residente123!`
- `residente3@mail.com` / `Residente123!`
- `residente4@mail.com` / `Residente123!`
- `residente5@mail.com` / `Residente123!`

## 🔍 Verificación

### Verificar Servicios
```powershell
ssh root@168.197.50.14 "docker-compose ps"
```

### Verificar Usuarios en PostgreSQL
```powershell
ssh root@168.197.50.14 "docker exec postgres_db psql -U postgres -d lobbysync -c 'SELECT id, email, role FROM users;'"
```

### Verificar Datos
```powershell
ssh root@168.197.50.14 "docker exec postgres_db psql -U postgres -d lobbysync -c 'SELECT COUNT(*) FROM buildings; SELECT COUNT(*) FROM units; SELECT COUNT(*) FROM common_areas; SELECT COUNT(*) FROM users;'"
```

## 🌐 URLs del Sistema

- **API Base**: http://168.197.50.14:8080
- **Swagger UI**: http://168.197.50.14:8080/swagger-ui.html
- **API Docs**: http://168.197.50.14:8080/v3/api-docs
- **Health**: http://168.197.50.14:8080/actuator/health (puede no estar habilitado)

## 📊 Base de Datos PostgreSQL

### Estructura
```
- buildings (3)
- units (12)
- common_areas (5)
- users (9)
- reservations
- invitations
- logbook_entries
- bills
- payments
```

### Conexión Manual
```bash
ssh root@168.197.50.14
docker exec -it postgres_db psql -U postgres -d lobbysync
```

## 🔥 Firebase

Los usuarios están almacenados en:
1. **Firebase Authentication**: Para login/autenticación
2. **PostgreSQL**: Para datos de la aplicación

**Ambos deben estar sincronizados** usando el script `sync-firebase-users.ps1`.

## ⚙️ Persistencia de Datos

### ✅ Los datos SE MANTIENEN cuando:
- Reinicias el VPS
- Reinicias los contenedores Docker
- Actualizas el backend

### ❌ Los datos SE PIERDEN cuando:
- Ejecutas `docker-compose down -v` (elimina volúmenes)
- Ejecutas el script `fix-vps.sh` (hace limpieza completa)

**Solución**: El script `fix-vps.sh` ahora **recarga automáticamente** los datos desde `seed-actual.sql` después de limpiar.

## 🛠️ Comandos Útiles

### Ver logs en tiempo real
```bash
ssh root@168.197.50.14 "docker logs -f lobbysync_backend"
```

### Reiniciar solo el backend (sin perder datos)
```bash
ssh root@168.197.50.14 "docker-compose restart backend"
```

### Recargar datos manualmente
```powershell
Get-Content seed-actual.sql | ssh root@168.197.50.14 "docker exec -i postgres_db psql -U postgres -d lobbysync"
```

### Verificar volúmenes Docker
```bash
ssh root@168.197.50.14 "docker volume ls"
ssh root@168.197.50.14 "docker volume inspect lobbysync-api_postgres_data"
```

## 🚨 Solución de Problemas

### La base de datos está vacía después de reiniciar
**Causa**: Se ejecutó `docker-compose down -v` por error
**Solución**: Ejecutar `.\fix-vps-remote.ps1` (recarga datos automáticamente)

### Los usuarios no pueden hacer login
**Causa**: Usuarios no están en Firebase
**Solución**: Ejecutar `.\sync-firebase-users.ps1`

### El backend no levanta
```bash
ssh root@168.197.50.14 "docker logs lobbysync_backend --tail 100"
```

### Verificar SSH está configurado (sin contraseña)
```powershell
ssh root@168.197.50.14 "echo 'SSH OK'"
```

## 📝 Notas Importantes

1. **SSH sin contraseña está configurado** ✅
2. **Los volúmenes Docker son persistentes** ✅
3. **Los datos se recargan automáticamente** en `fix-vps.sh` ✅
4. **Firebase y PostgreSQL deben estar sincronizados** ⚠️

## 🔄 Flujo Completo de Recuperación

```
1. .\fix-vps-remote.ps1
   ↓
2. Servicios levantados + Datos en PostgreSQL
   ↓
3. .\sync-firebase-users.ps1
   ↓
4. Usuarios sincronizados con Firebase
   ↓
5. ✅ Sistema funcionando completamente
```

---

**Última actualización**: 29 enero 2026
**VPS IP**: 168.197.50.14
**Puerto API**: 8080
