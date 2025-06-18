package ru.beeline.referenceservice.exception;

public class LoginAlreadyExistsException extends RuntimeException{

    public LoginAlreadyExistsException(String message) {
        super(message);
    }
}
