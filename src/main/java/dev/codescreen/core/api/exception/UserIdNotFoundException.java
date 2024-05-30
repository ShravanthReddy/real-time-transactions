package dev.codescreen.core.api.exception;

public class UserIdNotFoundException extends RuntimeException {

    private String code;
    public UserIdNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public UserIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
