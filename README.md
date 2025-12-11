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

1. **Crear proyecto en Firebase Console**
2. **Descargar serviceAccountKey.json**
3. **Guardar en**: `/Users/tu-usuario/Downloads/serviceAccountKey.json`
   - NO commitar a Git (protegido por .gitignore)

## 📚 API Endpoints

### Autenticación
- `POST /api/auth/sync` - Sincronizar usuario con Firebase

```bash
curl -X POST http://localhost:8080/api/auth/sync \
  -H "Content-Type: application/json" \
  -d '{
    "firebaseUid": "uid-from-firebase",
    "email": "user@example.com"
  }'
```

### Edificios
- `GET /api/v1/buildings` - Listar edificios
- `POST /api/v1/buildings` - Crear edificio
- `GET /api/v1/buildings/{id}` - Obtener detalles
- `PUT /api/v1/buildings/{id}` - Actualizar
- `DELETE /api/v1/buildings/{id}` - Eliminar

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
- **Host**: localhost:5432
- **Usuario**: admin_postgres
- **Contraseña**: postgres_db
- **Base de datos**: edificios_db

**Tablas principales**:
- `users` - Usuarios sincronizados desde Firebase
- `buildings` - Edificios/propiedades
- `bills` - Facturas

### MongoDB
- **Host**: localhost:27017
- **Usuario**: admin_mongo
- **Contraseña**: mongo_db
- **Base de datos**: admin_mongo

**Colecciones principales**:
- `access_logs` - Registros de acceso
- `parcels` - Entregas
- `asset_records` - Activos
- `maintenance_logs` - Mantenimiento

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
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://postgres_db:5432/edificios_db | URL PostgreSQL |
| `SPRING_MONGODB_HOST` | mongo_db | Host MongoDB |
| `FIREBASE_CONFIG_PATH` | /app/serviceAccountKey.json | Ruta credenciales Firebase |

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=BuildingControllerTest

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

### v1.0.0 - Firebase Integration Complete
- ✅ FirebaseTokenFilter - Validación de tokens
- ✅ UserService - Sincronización usuario-BD
- ✅ AuthController - Endpoint /api/auth/sync
- ✅ Todas las pruebas pasando
- ✅ Docker deployment funcional

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

**Última actualización**: 2025-12-11  
**Estado**: ✅ Production Ready
