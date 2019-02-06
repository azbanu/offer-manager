package com.worldpay.offermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OfferManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfferManagerApplication.class, args);
	}

}

