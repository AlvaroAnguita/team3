package com.prueba.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	private ApiInfo appInfo() {
		
		return new ApiInfo("Proyecto Tarea Grupal",
				"Proyecto donde se prueba las funcionalidades de Spring",
				"1.0",
				"Uso libre",
				new springfox.documentation.service.Contact("Team 3", "http://www.capgemini.com", "team3@capgemini.com"),
				"Licencia de pago",
				"https://www.google.es",
				Collections.emptyList());
		
	}

	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.prueba.controller"))
				.paths(PathSelectors.any())
				.build().apiInfo(appInfo());
		
	}
}
