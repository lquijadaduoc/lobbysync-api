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
                        .version("1.0.0")
                        .description("""
                                # LobbySync API - Sistema de Gestión de Edificios
                                
                                API REST completa para la administración de edificios residenciales y comerciales.
                                
                                ## Características Principales
                                
                                - **Core Module**: Gestión de edificios, unidades y usuarios
                                - **Access Control**: Control de acceso con QR y registro de entregas
                                - **Finance Module**: Gestión de gastos comunes, pagos y deudas
                                - **Maintenance Module**: Administración de activos y tickets de mantenimiento
                                
                                ## Arquitectura
                                
                                - Autenticación con Firebase Authentication
                                - PostgreSQL para entidades SQL
                                - MongoDB para logs y documentos flexibles
                                - Spring Boot 4.0.0 + Java 17
                                """)
                        .contact(new Contact()
                                .name("LobbySync Team")
                                .email("contacto@lobbysync.cl")
                                .url("https://lobbysync.cl"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.lobbysync.cl")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Firebase JWT Token")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
