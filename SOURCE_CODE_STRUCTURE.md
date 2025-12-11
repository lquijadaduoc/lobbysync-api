# NOTE: Source Code Location

Los archivos fuente Java del proyecto LobbySync Backend están disponibles en:

**Ruta Local**: `/Users/luisquijadamunoz/Documents/backend/src/main/java/cl/lobbysync/backend/`

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/cl/lobbysync/backend/
│   │   ├── BackendApplication.java
│   │   ├── controller/
│   │   │   ├── AuthController.java (Firebase auth sync endpoint)
│   │   │   ├── BuildingController.java
│   │   │   ├── AccessController.java
│   │   │   ├── ParcelController.java
│   │   │   ├── FinanceController.java
│   │   │   ├── AssetController.java
│   │   │   └── FileController.java
│   │   ├── service/
│   │   │   ├── UserService.java (Firebase user sync)
│   │   │   ├── BuildingService.java
│   │   │   ├── AccessService.java
│   │   │   ├── ParcelService.java
│   │   │   ├── FinanceService.java
│   │   │   ├── AssetService.java
│   │   │   └── StorageService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java (JPA)
│   │   │   ├── BuildingRepository.java (JPA)
│   │   │   ├── BillRepository.java (JPA)
│   │   │   ├── AccessLogRepository.java (Mongo)
│   │   │   ├── ParcelRepository.java (Mongo)
│   │   │   ├── AssetRepository.java (Mongo)
│   │   │   └── MaintenanceLogRepository.java (Mongo)
│   │   ├── model/
│   │   │   ├── sql/
│   │   │   │   ├── User.java
│   │   │   │   ├── Building.java
│   │   │   │   └── Bill.java
│   │   │   └── mongo/
│   │   │       ├── AccessLog.java
│   │   │       ├── Parcel.java
│   │   │       ├── AssetRecord.java
│   │   │       └── MaintenanceLog.java
│   │   ├── config/
│   │   │   ├── FirebaseConfig.java (Firebase Admin SDK initialization)
│   │   │   ├── SecurityConfig.java (Spring Security + Firebase filter)
│   │   │   ├── WebConfig.java (CORS configuration)
│   │   │   └── OpenApiConfig.java (Swagger documentation)
│   │   ├── filter/
│   │   │   └── FirebaseTokenFilter.java (JWT token validation)
│   │   ├── dto/
│   │   │   ├── UserSyncResponse.java
│   │   │   ├── AccessEntryRequest.java
│   │   │   ├── ParcelRequest.java
│   │   │   └── BillGenerationRequest.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       └── ResourceNotFoundException.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/cl/lobbysync/backend/
        └── BackendApplicationTests.java
```

## Importancia

Estos archivos contienen:
- ✅ Implementación completa de Firebase Authentication
- ✅ Validación de tokens Firebase
- ✅ Sincronización de usuarios a PostgreSQL
- ✅ 7 controllers REST con 14+ endpoints
- ✅ 7 servicios con lógica de negocio
- ✅ 7 repositorios (JPA + MongoDB)
- ✅ Configuración de seguridad
- ✅ Manejo de excepciones
- ✅ DTOs con validación

## Para Recuperar el Código

Si los archivos no están presentes, pueden ser descargados desde:
- El contenedor Docker en ejecución
- Un backup previo del proyecto
- Rebuilding desde el Dockerfile multi-stage que contiene la compilación

## Docker Build

El Dockerfile incluye:
```dockerfile
COPY src ./src
RUN mvn clean package -DskipTests
```

Esto significa que los fuentes son necesarios para la construcción de la imagen Docker.

**Nota**: El .gitignore protege archivos sensibles como `serviceAccountKey.json` que nunca deben ser commiteados.
