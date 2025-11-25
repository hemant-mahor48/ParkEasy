package com.parkeasy.parking_lot_service.exceptions;

public class ParkingLotNotFoundException extends RuntimeException{
    public ParkingLotNotFoundException(String message){
        super(message);
    }
}
