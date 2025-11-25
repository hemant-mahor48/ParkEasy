package com.parkeasy.parking_lot_service.exceptions;

import feign.FeignException;

public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException(String message){
        super(message);
    }
}
