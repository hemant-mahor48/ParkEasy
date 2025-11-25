package com.parkeasy.parking_lot_service.exceptions;

public class InvalidOwnerException extends RuntimeException{
    public InvalidOwnerException(String message){
        super(message);
    }
}
