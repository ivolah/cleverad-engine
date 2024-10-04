package it.cleverad.engine.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class CleveradInterruptedException extends InterruptedException {
    public CleveradInterruptedException(String str) {
        super(str);
    }
}