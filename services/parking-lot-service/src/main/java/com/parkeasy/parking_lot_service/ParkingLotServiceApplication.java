package com.parkeasy.parking_lot_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ParkingLotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingLotServiceApplication.class, args);
	}

}
