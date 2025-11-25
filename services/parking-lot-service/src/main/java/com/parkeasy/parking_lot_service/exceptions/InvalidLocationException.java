package com.parkeasy.parking_lot_service.exceptions;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException(String message){
        super(message);
    }
}
