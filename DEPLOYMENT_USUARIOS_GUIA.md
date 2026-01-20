# 🚀 DEPLOYMENT A PRODUCCIÓN - GUÍA RÁPIDA

## ✅ Cambios Implementados que se van a Desplegar

### Backend:
1. ✅ **UserService.java** - Métodos CRUD con Firebase
2. ✅ **UserController.java** - Endpoints completos
3. ✅ **DTOs** - UpdateUserRequest, ChangePasswordRequest
4. ✅ **CreateUserRequest** - Con soporte para unitId

### Funcionalidades Nuevas:
- 🔥 Crear usuarios en Firebase + PostgreSQL
- ✏️ Editar usuarios existentes
- 🔐 Cambiar contraseñas en Firebase
- 🗑️ Eliminar usuarios (Firebase + DB)
- 🏠 Asignar departamentos a residentes

---

## 🎯 PASOS PARA DESPLEGAR

### Opción 1: Script Automático (Recomendado)

```powershell
cd "C:\Users\Sebastian\Desktop\Examen Final\lobbysync-api"
.\deploy-produccion-usuarios.ps1
```

**El script hace:**
1. ✅ Compila el proyecto con Maven
2. ✅ Sube el JAR al VPS
3. ✅ Reinicia el contenedor Docker
4. ✅ Verifica logs

**Tiempo estimado:** 3-5 minutos

---

### Opción 2: Manual Paso a Paso

#### Paso 1: Compilar localmente

```powershell
cd "C:\Users\Sebastian\Desktop\Examen Final\lobbysync-api"
mvn clean package -DskipTests
```

**Resultado:** Se genera `target/backend-0.0.1-SNAPSHOT.jar`

---

#### Paso 2: Subir JAR al VPS

```powershell
# Usando pscp (PuTTY)
pscp -pw SebaErica12.18 target\backend-0.0.1-SNAPSHOT.jar root@168.197.50.14:/root/lobbysync-api/target/

# O usando WinSCP (interfaz gráfica)
# Host: 168.197.50.14
# Usuario: root
# Password: SebaErica12.18
# Subir archivo a: /root/lobbysync-api/target/
```

---

#### Paso 3: Conectar al VPS y reiniciar

```powershell
ssh root@168.197.50.14
# Password: SebaErica12.18
```

Luego ejecutar:

```bash
cd /root/lobbysync-api

# Detener contenedor actual
docker-compose down

# Iniciar con nuevo código
docker-compose up -d

# Ver logs
docker logs lobbysync-backend --tail 50 -f
```

---

## 🧪 VERIFICAR QUE FUNCIONA

### 1. Verificar que el backend está arriba

```bash
curl http://168.197.50.14:8080/actuator/health
```

**Respuesta esperada:**
```json
{"status":"UP"}
```

---

### 2. Probar endpoint de usuarios

```bash
curl http://168.197.50.14:8080/api/v1/users
```

**Debe retornar:** Lista de usuarios

---

### 3. Probar crear usuario (desde Postman o frontend)

**Endpoint:** `POST http://168.197.50.14:8080/api/v1/users`

**Body:**
```json
{
  "email": "test@lobbysync.com",
  "password": "test123",
  "firstName": "Test",
  "lastName": "User",
  "role": "RESIDENT",
  "phone": "+56912345678",
  "unitId": 1
}
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente en Firebase y PostgreSQL",
  "userId": 10,
  "firebaseUid": "xxxxxxxxxxx",
  "email": "test@lobbysync.com",
  "role": "RESIDENT"
}
```

---

### 4. Verificar en Firebase Console

Ir a: https://console.firebase.google.com/project/lobbysync-91db0/authentication/users

**Debe aparecer:** El usuario recién creado

---

### 5. Probar editar usuario

**Endpoint:** `PUT http://168.197.50.14:8080/api/v1/users/10`

**Body:**
```json
{
  "firstName": "Test Updated",
  "phone": "+56987654321"
}
```

---

### 6. Probar cambiar contraseña

**Endpoint:** `POST http://168.197.50.14:8080/api/v1/users/10/change-password`

**Body:**
```json
{
  "newPassword": "newpass123"
}
```

---

### 7. Probar eliminar usuario

**Endpoint:** `DELETE http://168.197.50.14:8080/api/v1/users/10`

**Respuesta esperada:**
```json
{
  "message": "Usuario eliminado exitosamente"
}
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: "Container lobbysync-backend is not running"

```bash
cd /root/lobbysync-api
docker-compose up -d
docker logs lobbysync-backend --tail 100
```

---

### Error: "Connection refused"

**Verificar que el puerto 8080 está abierto:**

```bash
netstat -tuln | grep 8080
```

**Si no está, reiniciar contenedor:**

```bash
docker restart lobbysync-backend
```

---

### Error al compilar Maven

**Limpiar y volver a compilar:**

```powershell
mvn clean
mvn package -DskipTests -X
```

**Ver errores específicos en output**

---

### Error: "Firebase initialization failed"

**Verificar que existe serviceAccountKey.json:**

```bash
ssh root@168.197.50.14
ls -la /root/lobbysync-api/serviceAccountKey.json
```

**Si no existe, subirlo:**

```powershell
pscp -pw SebaErica12.18 serviceAccountKey.json root@168.197.50.14:/root/lobbysync-api/
```

---

## 📊 MONITOREO POST-DEPLOYMENT

### Ver logs en tiempo real:

```bash
ssh root@168.197.50.14
docker logs -f lobbysync-backend
```

**Buscar estas líneas:**
```
✅ Started BackendApplication
✅ Tomcat started on port(s): 8080
✅ Firebase initialized successfully
```

---

### Verificar estado de contenedores:

```bash
docker ps
```

**Debe mostrar:**
```
CONTAINER ID   IMAGE              STATUS         PORTS
xxxxxxxxx      lobbysync-backend  Up X minutes   0.0.0.0:8080->8080/tcp
xxxxxxxxx      postgres:15        Up X minutes   5432/tcp
xxxxxxxxx      mongo:7            Up X minutes   27017/tcp
```

---

### Probar desde el frontend:

1. Abrir frontend: http://localhost:5173 (o tu URL)
2. Login como admin
3. Ir a `/admin/users`
4. Intentar crear un usuario nuevo
5. Verificar que se crea en Firebase y PostgreSQL

---

## ✅ CHECKLIST POST-DEPLOYMENT

- [ ] Backend responde en `http://168.197.50.14:8080`
- [ ] Swagger UI accesible en `/swagger-ui.html`
- [ ] Endpoint GET `/api/v1/users` funciona
- [ ] Endpoint POST `/api/v1/users` crea usuario en Firebase
- [ ] Usuario aparece en Firebase Console
- [ ] Endpoint PUT `/api/v1/users/{id}` actualiza usuario
- [ ] Endpoint DELETE `/api/v1/users/{id}` elimina usuario
- [ ] Endpoint POST `/change-password` cambia contraseña
- [ ] Frontend puede crear usuarios con departamento
- [ ] Frontend puede editar usuarios
- [ ] Frontend puede cambiar contraseñas
- [ ] Frontend puede eliminar usuarios

---

## 🎉 ¡DEPLOYMENT EXITOSO!

Si todos los checks están ✅, el sistema está **100% operativo** en producción.

El frontend en `http://localhost:5173` ya puede usar todas las funcionalidades de gestión de usuarios conectándose al backend en producción.

---

## 📞 CONTACTO

Si hay problemas durante el deployment, revisar:
1. Logs del backend: `docker logs lobbysync-backend`
2. Logs de PostgreSQL: `docker logs postgres_db`
3. Estado de contenedores: `docker ps -a`
4. Conectividad: `curl http://168.197.50.14:8080/actuator/health`
