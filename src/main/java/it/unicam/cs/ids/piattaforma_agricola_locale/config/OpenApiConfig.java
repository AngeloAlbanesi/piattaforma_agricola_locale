package it.unicam.cs.ids.piattaforma_agricola_locale.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configurazione OpenAPI per la documentazione automatica delle API.
 *
 * Parte della Fase 4: Testing & Documentation
 *
 * Fornisce documentazione interattiva Swagger UI per tutte le API della
 * piattaforma agricola locale.
 * La documentazione è accessibile all'indirizzo: /swagger-ui.html
 * L'API definition in formato JSON è disponibile all'indirizzo: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI piattaformaAgricolaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Piattaforma Agricola Locale API")
                        .description("API complete per la gestione dell'e-commerce agricola locale. " +
                                "Include gestione prodotti, ordini, utenti, pagamenti, certificazioni, " +
                                "processi di trasformazione, eventi e pacchetti.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Team Sviluppo Piattaforma Agricola")
                                .email("support@piattaforma-agricola.it")
                                .url("https://piattaforma-agricola.it"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.piattaforma-agricola.it").description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Inserire il JWT token per autenticarsi. " +
                        "Formato: Bearer {your-jwt-token}");
    }
}
