package it.cleverad.engine.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class BrandBuddiesMediaTargetException extends RuntimeException {
    public BrandBuddiesMediaTargetException(String str) {
        super(str);
    }
}