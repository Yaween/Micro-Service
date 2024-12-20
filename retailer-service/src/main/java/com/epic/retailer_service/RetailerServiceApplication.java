package com.epic.retailer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication
@EnableFeignClients
public class RetailerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetailerServiceApplication.class, args);
	}

}
