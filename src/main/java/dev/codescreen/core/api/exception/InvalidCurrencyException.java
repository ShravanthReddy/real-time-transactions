package dev.codescreen.core.api.exception;

public class InvalidCurrencyException extends RuntimeException {
    private String code;
    public InvalidCurrencyException(String message, String code) {
        super(message);
        this.code = code;
    }

    public InvalidCurrencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
