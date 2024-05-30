package dev.codescreen.core.api.exception;

public class ServerInternalErrorException extends RuntimeException {

    private String code;
    public ServerInternalErrorException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ServerInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
