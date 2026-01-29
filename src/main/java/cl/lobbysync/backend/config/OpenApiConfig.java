package cl.lobbysync.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LobbySync Backend API")
                        .version("2.0.0")
                        .description("""
                                # LobbySync API - Sistema de Gestión de Edificios
                                
                                API REST completa para la administración de edificios residenciales y comerciales con Firebase Authentication.
                                
                                ## 🔐 Autenticación
                                
                                Esta API soporta **2 métodos de autenticación**:
                                
                                ### 1. Google Sign-In (Recomendado)
                                
                                **Frontend:**
                                1. Integra Google Sign-In button (Google Identity Services)
                                2. Usuario hace clic y selecciona cuenta Google
                                3. Obtienes el `idToken` desde Google
                                4. Envías el token al backend: `POST /api/auth/google`
                                
                                **Backend:**
                                - Valida el token con Google API
                                - Busca o crea el usuario en PostgreSQL automáticamente
                                - Retorna un JWT custom válido por 24 horas
                                
                                **Ejemplo:**
                                ```json
                                POST /api/auth/google
                                {
                                  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6..."
                                }
                                
                                Response:
                                {
                                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
                                  "user": {
                                    "id": 1,
                                    "email": "usuario@gmail.com",
                                    "firstName": "Juan",
                                    "lastName": "Pérez",
                                    "role": "RESIDENT"
                                  },
                                  "message": "Login con Google exitoso"
                                }
                                ```
                                
                                ### 2. Login Simple (Email/Password)
                                
                                Para usuarios creados manualmente en el sistema:
                                
                                **Ejemplo:**
                                ```json
                                POST /api/auth/login
                                {
                                  "email": "admin@lobbysync.com",
                                  "password": "Lobbysync_2026*"
                                }
                                ```
                                
                                ### Usando el Token JWT
                                
                                Todos los endpoints protegidos requieren el JWT en el header:
                                ```
                                Authorization: Bearer <tu-jwt-token>
                                ```
                                
                                ### Usuarios de Prueba (Firebase Auth)
                                
                                - **Admin**: admin@lobbysync.com (contraseña: Lobbysync_2026*)
                                - **Conserje**: concierge@lobbysync.com (contraseña: Lobbysync_2026*)
                                - **Residente**: resident@lobbysync.com (contraseña: Lobbysync_2026*)
                                
                                ## 📚 Módulos Disponibles
                                
                                ### 👥 Gestión de Usuarios
                                - Crear usuarios en Firebase + PostgreSQL simultáneamente
                                - Sincronización automática con Firebase Authentication
                                - Roles: SUPER_ADMIN, ADMIN, CONCIERGE, RESIDENT
                                
                                ### 🏢 Gestión de Edificios y Unidades
                                - CRUD completo de edificios/propiedades
                                - Gestión de unidades/departamentos por edificio
                                - Seguimiento de ocupación y residentes
                                
                                ### 📋 Bitácora (Logbook)
                                - Sistema de registro para conserjes
                                - Notas con timestamp y usuario
                                - Búsqueda por fecha
                                
                                ### 🔑 Control de Acceso
                                - Registro de entrada/salida con QR
                                - Historial de visitas
                                - Gestión de invitaciones
                                
                                ### 📦 Gestión de Parcelas
                                - Registro de entregas y paquetería
                                - Notificaciones a residentes
                                - Control de retiros
                                
                                ### 💰 Finanzas
                                - Generación de gastos comunes
                                - Seguimiento de pagos
                                - Control de deudas
                                
                                ### 🛠️ Mantenimiento
                                - Registro de activos del edificio
                                - Tickets de mantenimiento
                                - Historial de reparaciones
                                
                                ## 🗄️ Base de Datos
                                
                                - **PostgreSQL**: Datos estructurados (users, buildings, units, bills)
                                - **MongoDB**: Logs y documentos flexibles (access_logs, parcels, maintenance)
                                
                                ## 🌐 Servidores
                                
                                - **Desarrollo**: http://localhost:8080
                                - **Producción**: http://168.197.50.14:8080
                                """)
                        .contact(new Contact()
                                .name("Luis Quijada Munoz")
                                .email("luisquijadaduoc@gmail.com")
                                .url("https://github.com/lquijadaduoc"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("http://168.197.50.14:8080")
                                .description("Production Server (VPS)")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Firebase ID Token - Obtener desde Firebase Authentication después de login")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
