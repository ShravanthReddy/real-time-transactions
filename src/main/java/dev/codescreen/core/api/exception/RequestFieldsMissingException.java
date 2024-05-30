package dev.codescreen.core.api.exception;

public class RequestFieldsMissingException extends RuntimeException {

    private String code;
    public RequestFieldsMissingException(String message, String code) {
        super(message);
        this.code = code;
    }

    public RequestFieldsMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }

}
