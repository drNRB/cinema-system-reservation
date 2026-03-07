package com.portfolio.cinema_system_reservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cinemaSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cinema System Reservation API")
                        .description("Backend REST API for cinema seat reservation system.")
                        .version("v1.0.0"));
    }
}