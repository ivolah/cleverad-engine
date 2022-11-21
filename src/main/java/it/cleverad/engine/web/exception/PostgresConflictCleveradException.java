package it.cleverad.engine.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PostgresConflictCleveradException extends RuntimeException {
    public PostgresConflictCleveradException(Exception e) {
        super(e);
    }
}
