package com.kaushik.userregistration.user;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("email already registered: " + email);
    }
}
