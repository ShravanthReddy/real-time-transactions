package dev.codescreen.core.api.exception;

public class InvalidMessageIdException extends RuntimeException {

    private String code;
    public InvalidMessageIdException(String message, String code) {
        super(message);
        this.code = code;
    }

    public InvalidMessageIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
