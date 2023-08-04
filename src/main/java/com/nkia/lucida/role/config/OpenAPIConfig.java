package com.nkia.lucida.role.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenAPIConfig {

  @Bean
  public OpenAPI openAPI(@Value("v1") String appVersion) {

    Info info = new Info().title("Role Service").version(appVersion)
        .description("Role Service의 API 입니다.")
        .termsOfService("http://swagger.io/terms/")
        .license(new License().name("Nkia License").url("http://swagger.io/terms/"));

    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER).name("Authorization");

    return new OpenAPI()
        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
        .info(info);
  }
}