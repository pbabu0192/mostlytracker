package com.mosltyai.mostlytracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Component
@EnableSwagger2
public class DocketConfig {

  @Value("${tracker.swagger.enabled:true}")
  private boolean isSwaggerEnabled;

  @Bean
  public Docket employeeDocket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(
            new ApiInfoBuilder()
                .title("Mostly Tracker")
                .description("Project tracking system")
                .version("1.0.0")
                .build())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/project/**"))
        .build()
        .enable(isSwaggerEnabled)
        .securityContexts(
            List.of(
                SecurityContext.builder()
                    .securityReferences(
                        List.of(new SecurityReference("basicAuth", new AuthorizationScope[0])))
                    .build()))
        .securitySchemes(List.of(new BasicAuth("basicAuth")));
  }
}
