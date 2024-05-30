package dev.codescreen.core.api.exception;

public class WrongTransactionTypeException extends RuntimeException {

    private String code;
    public WrongTransactionTypeException(String message, String code) {
        super(message);
        this.code = code;
    }

    public WrongTransactionTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
