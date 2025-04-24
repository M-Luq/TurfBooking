package com.cts.turfservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy
public class TurfServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurfServiceApplication.class, args);
	}
    
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
}
