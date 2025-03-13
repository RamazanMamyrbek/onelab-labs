package com.onelab.courses_service;

import org.onelab.common.feign.EnableFeign;
import org.onelab.common.security.EnableSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.onelab.courses_service.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.onelab.courses_service.repository.elastic")
@EnableFeign
@EnableSecurity
public class CoursesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoursesServiceApplication.class, args);
	}

}
