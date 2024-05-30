package dev.codescreen.core.api.exception;

public class NotValidAmountException extends RuntimeException {
    private String code;
    public NotValidAmountException(String message, String code) {
        super(message);
        this.code = code;
    }

    public NotValidAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
