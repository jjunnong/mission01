package org.ohgiraffers.board.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "Board Mission🤔", description = "Board Mission Api 명세", version = "v1"))

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi firstOpenApi(){
        String[] path = {
                "org.ohgiraffers.board.controller"
        };

        return GroupedOpenApi.builder()
                .group("1. 게시글 관리")
                .packagesToScan(path)
                .build();
    }
    @Bean
    public GroupedOpenApi secondOpenApi(){
        String[] path = {
                ""
        };

        return GroupedOpenApi.builder()
                .group("2. 아직 미정")
                .packagesToScan(path)
                .build();
    }
}
