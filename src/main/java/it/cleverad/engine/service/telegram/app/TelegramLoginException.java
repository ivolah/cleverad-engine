package it.cleverad.engine.service.telegram.app;

public class TelegramLoginException extends Exception {
    public TelegramLoginException(String message) {
        super(message);
    }
}