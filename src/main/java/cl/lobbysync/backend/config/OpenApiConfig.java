package cl.lobbysync.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LobbySync Backend API")
                        .version("1.0.0")
                        .description("REST API for building management system with Firebase authentication")
                        .contact(new Contact()
                                .name("LobbySync Team")
                                .url("https://lobbysync.cl")));
    }
}
