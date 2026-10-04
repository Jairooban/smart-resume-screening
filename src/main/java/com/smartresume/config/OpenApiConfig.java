package com.smartresume.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger documentation configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartResumeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Resume Screening System API")
                        .description("REST API documentation for automated resume parsing, NLP skill extraction, TF-IDF scoring, and candidate ranking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Smart Resume Screening Team")
                                .email("hr@company.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }
}
