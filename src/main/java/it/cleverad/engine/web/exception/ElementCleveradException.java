package it.cleverad.engine.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class ElementCleveradException extends RuntimeException {
    public ElementCleveradException(Long id) {
        super("Element with ID " + id + " not found.");
    }
}



