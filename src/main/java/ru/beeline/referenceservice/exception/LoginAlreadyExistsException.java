/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.exception;

public class LoginAlreadyExistsException extends RuntimeException{

    public LoginAlreadyExistsException(String message) {
        super(message);
    }
}
