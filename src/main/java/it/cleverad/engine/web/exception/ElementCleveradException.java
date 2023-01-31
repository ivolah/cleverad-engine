package it.cleverad.engine.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class ElementCleveradException extends RuntimeException {
    public ElementCleveradException(String str, Long id) {
        super(str + " element with ID " + id + " not found.");
    }

    public ElementCleveradException(String str, String localizedMessage) {
        super("Eccezione in " + str + " di tipo " + localizedMessage);
    }

}
