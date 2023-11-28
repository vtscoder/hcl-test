package com.db.dataplatform.techtest.server.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

public class DownStreamPushDataException extends Exception {
    public DownStreamPushDataException(final String message) {
        super(message);
    }
}
