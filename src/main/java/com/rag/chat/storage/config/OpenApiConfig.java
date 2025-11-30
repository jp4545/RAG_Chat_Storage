package com.rag.chat.storage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RAG Chat Storage API")
                        .description("APIs for users, sessions, documents, and embeddings")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("apiKey")
                        .addList("apiSecret"))
                .components(new Components()
                        .addSecuritySchemes("apiKey",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-KEY")
                                        .description("Default: my-api-key-123"))
                        .addSecuritySchemes("apiSecret",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-SECRET")
                                        .description("Default: my-secret-456"))
                );
    }
}