package com.gym.class_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClassMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassMicroserviceApplication.class, args);
	}

}
