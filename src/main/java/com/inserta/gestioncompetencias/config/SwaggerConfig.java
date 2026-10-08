package com.inserta.gestioncompetencias.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configura OpenAPI/Swagger con autenticacion Bearer JWT.
 * Sigue el mismo esquema utilizado en la FASE 8 del proyecto del curso:
 * @SecurityScheme declara el boton Authorize y @OpenAPIDefinition aplica
 * el esquema de seguridad a la API.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "SkillBridge",
        version = "1.0",
        description = "SkillBridge para la gestion de capacidad y competencias tecnicas"),
    security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class SwaggerConfig {
}
