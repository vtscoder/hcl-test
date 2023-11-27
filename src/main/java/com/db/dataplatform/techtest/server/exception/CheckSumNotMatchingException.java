package com.db.dataplatform.techtest.server.exception;

public class CheckSumNotMatchingException extends Exception {
    public CheckSumNotMatchingException(final String message) {
        super(message);
    }
}
