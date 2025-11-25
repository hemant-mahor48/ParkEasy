package com.parkeasy.parking_lot_service.exceptions;

public class MissingHeaderException extends RuntimeException {
    public MissingHeaderException(String message){
        super(message);
    }
}
