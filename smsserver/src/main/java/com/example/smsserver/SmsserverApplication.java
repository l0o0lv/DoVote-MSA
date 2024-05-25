package com.example.smsserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SmsserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmsserverApplication.class, args);
	}

}
